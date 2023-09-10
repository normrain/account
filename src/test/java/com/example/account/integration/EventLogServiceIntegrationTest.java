package com.example.account.integration;

import com.example.account.domain.logs.entity.EventLog;
import com.example.account.domain.logs.repository.EventLogRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = EventLogServiceIntegrationTest.Initializer.class)
@TestPropertySource(properties = {
        "spring.rabbitmq.host=localhost",
        "spring.rabbitmq.port=5672",
        "spring.rabbitmq.username=test",
        "spring.rabbitmq.password=test",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=password"
})
public class EventLogServiceIntegrationTest {

    public static GenericContainer<?> rabbitMqContainer = new GenericContainer<>("rabbitmq:3.8.21-management")
            .withExposedPorts(5672)
            .withEnv("RABBITMQ_DEFAULT_USER", "test")
            .withEnv("RABBITMQ_DEFAULT_PASS", "test")
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private Queue queue;

    @Test
    public void testSaveMessageToLog() throws InterruptedException {
        // Send a message to the RabbitMQ queue
        EventLog eventLog = new EventLog("Test Message");
        String message = null;
        try {
            message = objectMapper.writeValueAsString(eventLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rabbitTemplate.convertAndSend(queue.getName(), message);

        // Wait for the message to be processed and saved to the database
        Thread.sleep(1000); // Adjust the sleep time as needed

        // Retrieve the saved message from the database
        EventLog savedEventLog = eventLogRepository.findAll().get(0);

        // Assert the results
        assertThat(savedEventLog).isNotNull();
        assertThat(savedEventLog.getMessage()).isEqualTo(eventLog.getMessage());
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
