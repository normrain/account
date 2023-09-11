package com.example.account.domain.logs.service;


import com.example.account.domain.logs.entity.EventLog;
import com.example.account.domain.logs.repository.EventLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class EventLogService {

    private final EventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "rabbitmq.queue")
    @Transactional
    public void saveMessageToLog(String in) throws JsonProcessingException {
        EventLog eventLog = objectMapper.readValue(in, EventLog.class);
        eventLogRepository.insert(eventLog);
    }
}
