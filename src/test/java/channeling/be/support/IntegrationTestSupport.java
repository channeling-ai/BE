package channeling.be.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import redis.embedded.RedisServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Testcontainers
public abstract class IntegrationTestSupport {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("channeling_test")
            .withUsername("test")
            .withPassword("test");

    static RedisServer redisServer;

    @BeforeAll
    static void startContainers() {
        postgres.start();

        try {
            redisServer = new RedisServer(16379);
            redisServer.start();
        } catch (Exception e) {
            // Redis already running on this port, ignore
        }
    }

    @AfterAll
    static void stopContainers() {
        try {
            if (redisServer != null && redisServer.isActive()) {
                redisServer.stop();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
