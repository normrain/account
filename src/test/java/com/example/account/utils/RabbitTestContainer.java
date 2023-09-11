package com.example.account.utils;

import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RabbitTestContainer extends RabbitMQContainer {

    private static final String IMAGE_VERSION = "rabbitmq:3-management-alpine";
    private static RabbitTestContainer container;

    private RabbitTestContainer() {
        super(IMAGE_VERSION);
    }

    public static RabbitTestContainer getInstance() {
        if (container == null) {
            container = new RabbitTestContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("RMQ_HOST", container.getHost());
        System.setProperty("RMQ_PORT", container.getMappedPort(5672).toString());
        System.setProperty("RMQ_USERNAME", container.getAdminUsername());
        System.setProperty("RMQ_PASSWORD", container.getAdminPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
