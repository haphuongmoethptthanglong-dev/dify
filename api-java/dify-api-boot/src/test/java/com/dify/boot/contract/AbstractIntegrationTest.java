package com.dify.boot.contract;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Shared base class for integration tests.
 *
 * <p>Manages singleton PostgreSQL (postgres:15-alpine) and Redis (redis:6-alpine)
 * containers that are started once per JVM and reused across all test classes.
 * Containers are started eagerly in a static initializer block so that every
 * subclass — regardless of Spring ApplicationContext caching — references the
 * same container instances on the same ports.
 *
 * <p>Spring connection properties are injected dynamically via {@link DynamicPropertySource},
 * eliminating hardcoded Docker network IPs.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES;
    @SuppressWarnings("resource")
    static final GenericContainer<?> REDIS;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("dify")
                .withUsername("postgres")
                .withPassword("difyai123456");
        POSTGRES.start();

        REDIS = new GenericContainer<>("redis:6-alpine")
                .withExposedPorts(6379);
        REDIS.start();
    }

    @DynamicPropertySource
    static void containerProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL datasource
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        // Redis
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }
}
