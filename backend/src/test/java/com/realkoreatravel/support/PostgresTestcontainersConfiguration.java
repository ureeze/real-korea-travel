package com.realkoreatravel.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/** Repository·Integration 테스트가 운영과 동일한 PostgreSQL을 사용하도록 Container를 등록한다. */
@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestcontainersConfiguration {

    /** 테스트용 PostgreSQL Container를 생성하고 Spring datasource 연결 정보로 제공한다. */
    @ServiceConnection
    @Bean(destroyMethod = "stop")
    @SuppressWarnings("resource")
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:18.4")
                .withDatabaseName("realkorea")
                .withUsername("realkorea")
                .withPassword("realkorea1234");
    }
}
