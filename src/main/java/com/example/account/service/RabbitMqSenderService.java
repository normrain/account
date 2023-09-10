package com.example.account.service;

import com.example.account.domain.logs.entity.EventLog;
import com.example.account.util.enums.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class RabbitMqSenderService {

    private final AmqpTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Queue queue;

    public void sendMessageToQueue(UUID objectId, EventType eventType) {
        EventLog event = EventLog.builder()
                .objectId(objectId)
                .eventType(eventType)
                .build();
        try {
            rabbitTemplate.convertAndSend(queue.getName(),objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Json Processing failed");
        }
    }

}
