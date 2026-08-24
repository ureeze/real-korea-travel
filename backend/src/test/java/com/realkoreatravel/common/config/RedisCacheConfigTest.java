package com.realkoreatravel.common.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/** Redis CacheManager가 프로젝트에서 사용하는 캐시 이름을 등록하는지 검증한다. */
class RedisCacheConfigTest {

    /** Redis CacheManager에 애플리케이션에서 사용하는 세 가지 캐시가 등록되는지 확인한다. */
    @Test
    @DisplayName("장소 목록·상세·검색 캐시를 등록한다")
    void cacheManagerRegistersApplicationCaches() {
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);

        CacheManager cacheManager = new RedisCacheConfig().cacheManager(connectionFactory);

        assertThat(cacheManager.getCache(RedisCacheConfig.CacheNames.PLACE_LIST)).isNotNull();
        assertThat(cacheManager.getCache(RedisCacheConfig.CacheNames.PLACE_DETAIL)).isNotNull();
        assertThat(cacheManager.getCache(RedisCacheConfig.CacheNames.SEARCH_RESULT)).isNotNull();
    }
}
