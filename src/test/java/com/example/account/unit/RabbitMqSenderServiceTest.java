package com.example.account.unit;

import com.example.account.domain.logs.entity.EventLog;
import com.example.account.util.enums.EventType;
import com.example.account.service.RabbitMqSenderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RabbitMqSenderServiceTest {

    @InjectMocks
    private RabbitMqSenderService rabbitMqSenderService;

    @Mock
    private AmqpTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Queue queue;

    @Test
    public void testSendMessageToQueue() throws JsonProcessingException {
        UUID objectId = UUID.randomUUID();
        EventType eventType = EventType.CREATION;
        EventLog expectedEventLog = EventLog.builder()
                .objectId(objectId)
                .eventType(eventType)
                .build();
        String jsonEventLog = "{\"objectId\":\"" + objectId + "\",\"eventType\":\"" + eventType + "\"}";

        when(objectMapper.writeValueAsString(expectedEventLog)).thenReturn(jsonEventLog);
        when(queue.getName()).thenReturn("testQueue");

        rabbitMqSenderService.sendMessageToQueue(objectId, eventType);

        verify(rabbitTemplate, times(1)).convertAndSend("testQueue", jsonEventLog);
    }

    @Test
    public void testSendMessageToQueueJsonProcessingException() throws JsonProcessingException {
        UUID objectId = UUID.randomUUID();
        EventType eventType = EventType.CREATION;

        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        rabbitMqSenderService.sendMessageToQueue(objectId, eventType);
    }
}
