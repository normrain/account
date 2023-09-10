package com.example.account;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestDatabaseConfiguration {

    private static final String POSTGRES_IMAGE = "postgres:15"; // Use the desired PostgreSQL version

    @Bean
    @Primary
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName("testdb") // Set the test database name
                .withUsername("testuser")   // Set the test database username
                .withPassword("testpass");  // Set the test database password
        container.start();
        return container;
    }
}
