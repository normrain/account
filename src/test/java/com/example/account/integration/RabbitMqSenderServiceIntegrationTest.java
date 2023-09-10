package com.example.account.integration;

import com.example.account.domain.logs.entity.EventLog;
import com.example.account.util.enums.EventType;
import com.example.account.service.RabbitMqSenderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = RabbitMqSenderServiceIntegrationTest.Initializer.class)
@TestPropertySource(properties = {
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672",
        "spring.rabbitmq.username=test",
        "spring.rabbitmq.password=test"
})
public class RabbitMqSenderServiceIntegrationTest {

    public static GenericContainer<?> rabbitMqContainer = new GenericContainer<>("rabbitmq:3.8.21-management")
            .withExposedPorts(5672)
            .withEnv("RABBITMQ_DEFAULT_USER", "test")
            .withEnv("RABBITMQ_DEFAULT_PASS", "test")
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private RabbitMqSenderService rabbitMqSenderService;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Queue queue;

    @Test
    public void testSendMessageToQueue() {
        // Set up test data
        UUID objectId = UUID.randomUUID();
        EventType eventType = EventType.CREATION;

        // Call the method to be tested
        rabbitMqSenderService.sendMessageToQueue(objectId, eventType);

        // Receive and deserialize the message from the queue
        String message = (String) rabbitTemplate.receiveAndConvert(queue.getName());

        // Deserialize the message into an EventLog object
        EventLog eventLog = null;
        try {
            eventLog = objectMapper.readValue(message, EventLog.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Assert the results
        assertThat(eventLog).isNotNull();
        assertThat(eventLog.getObjectId()).isEqualTo(objectId);
        assertThat(eventLog.getEventType()).isEqualTo(eventType);
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.rabbitmq.host=" + rabbitMqContainer.getContainerIpAddress(),
                    "spring.rabbitmq.port=" + rabbitMqContainer.getFirstMappedPort()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
