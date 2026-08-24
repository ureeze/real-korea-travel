package com.realkoreatravel.common.config;

import java.time.Duration;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 애플리케이션의 조회 응답을 Redis에 저장하기 위한 공통 캐시 설정이다.
 *
 * <p>서비스 메서드의 {@code @Cacheable}과 연결되는 캐시 이름별 TTL과
 * Redis key·value 직렬화 방식을 한 곳에서 관리한다.</p>
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
public class RedisCacheConfig {

    /** 별도 TTL이 없는 캐시와 장소 목록 캐시의 기본 보관 시간. */
    private static final Duration PLACE_LIST_TTL = Duration.ofMinutes(5);

    /** 장소 상세 정보는 목록보다 긴 10분 동안 보관한다. */
    private static final Duration PLACE_DETAIL_TTL = Duration.ofMinutes(10);

    /** 검색 결과는 검색 조건이 다양하므로 상대적으로 짧은 3분 동안 보관한다. */
    private static final Duration SEARCH_RESULT_TTL = Duration.ofMinutes(3);

    /**
     * Redis 연결 정보와 캐시별 TTL을 사용해 애플리케이션의 CacheManager를 생성한다.
     *
     * <p>명시적으로 등록한 장소 목록·상세·검색 캐시는 각각 용도별 TTL을 사용하며,
     * 등록되지 않은 캐시는 기본 TTL인 5분을 사용한다.</p>
     *
     * @param connectionFactory Spring Boot가 구성한 Redis 연결 팩토리
     * @return Redis 기반 캐시 관리자
     */
    @Bean
    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfiguration = createConfiguration(
                PLACE_LIST_TTL
        );
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                CacheNames.PLACE_LIST, createConfiguration(PLACE_LIST_TTL),
                CacheNames.PLACE_DETAIL, createConfiguration(PLACE_DETAIL_TTL),
                CacheNames.SEARCH_RESULT, createConfiguration(SEARCH_RESULT_TTL)
        );
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * 하나의 캐시 영역에 적용할 만료 시간과 직렬화 규칙을 조합한다.
     *
     * <p>캐시 키는 사람이 확인하기 쉬운 문자열로 저장하고, 응답 객체는
     * Spring Data Redis의 JSON serializer로 저장해 Redis에 직접 접근해도
     * 내용을 확인할 수 있도록 한다.</p>
     *
     * @param ttl 해당 캐시 영역의 데이터 보관 시간
     * @return 지정한 정책이 적용된 Redis 캐시 설정
     */
    private RedisCacheConfiguration createConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()
                ))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        RedisSerializer.json()
                ));
    }

    /** 서비스의 {@code @Cacheable}과 CacheManager가 공유하는 캐시 영역 이름을 관리한다. */
    public static final class CacheNames {
        /** 장소 목록 응답 캐시 영역 이름. */
        public static final String PLACE_LIST = "placeList";

        /** 장소 상세 응답 캐시 영역 이름. */
        public static final String PLACE_DETAIL = "placeDetail";

        /** 검색 결과 응답 캐시 영역 이름. */
        public static final String SEARCH_RESULT = "searchResult";

        /** 인스턴스 생성을 막고 상수만 제공한다. */
        private CacheNames() {
        }
    }
}
