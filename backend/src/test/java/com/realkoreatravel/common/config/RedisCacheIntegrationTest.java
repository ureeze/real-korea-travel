package com.realkoreatravel.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.realkoreatravel.place.dto.PlaceListResponse;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * 실제 Redis 컨테이너를 사용해 캐시 저장·조회와 JSON 역직렬화를 검증한다.
 *
 * <p>Redis 전용 Serializer 설정이 실제 Redis 엔진에서도 동작하고,
 * 저장한 응답이 원래 DTO 타입으로 복원되는지 확인하는 통합 테스트다.</p>
 */
@Testcontainers
class RedisCacheIntegrationTest {

    /** Testcontainers가 테스트 전후로 시작·종료하는 Redis 컨테이너다. */
    @SuppressWarnings("resource")
    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:8.10")
            .withExposedPorts(6379);

    /**
     * Redis에 저장한 장소 목록 응답이 동일한 DTO 타입으로 복원되는지 확인한다.
     *
     * <p>테스트 전용 Redis 연결과 CacheManager를 구성한 뒤, 캐시에 응답을 저장하고
     * 명시한 DTO 타입으로 조회했을 때 원본과 같은 값이 반환되는지 검증한다.</p>
     */
    @Test
    void cacheStoresAndReadsPlaceListResponse() {
        // Testcontainers가 호스트에 매핑한 포트를 사용해 실제 Redis에 연결한다.
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(
                REDIS.getHost(),
                REDIS.getMappedPort(6379)
        );
        connectionFactory.afterPropertiesSet();

        // 운영 코드와 동일한 RedisCacheConfig으로 캐시 관리자와 대상 캐시를 생성한다.
        CacheManager cacheManager = new RedisCacheConfig().cacheManager(connectionFactory);
        Cache cache = Objects.requireNonNull(
                cacheManager.getCache(RedisCacheConfig.CacheNames.PLACE_LIST),
                "장소 목록 캐시가 등록되지 않았습니다."
        );

        // 캐시에 저장할 테스트용 장소 목록 응답을 구성한다.
        PlaceListResponse expected = PlaceListResponse.builder()
                .places(List.of())
                .page(0)
                .size(20)
                .totalElements(0)
                .totalPages(0)
                .build();

        try {
            // Redis에 응답을 저장한 뒤 지정한 DTO 타입으로 다시 읽는다.
            cache.put("test-key", expected);

            // JSON 직렬화·역직렬화 후에도 원본 응답과 같은 값인지 검증한다.
            assertThat(cache.get("test-key", PlaceListResponse.class)).isEqualTo(expected);
        } finally {
            // 테스트가 성공하거나 실패해도 Redis 연결을 명시적으로 종료한다.
            connectionFactory.destroy();
        }
    }
}
