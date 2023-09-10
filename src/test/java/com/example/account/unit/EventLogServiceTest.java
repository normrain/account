package com.example.account.unit;

import com.example.account.domain.logs.entity.EventLog;
import com.example.account.domain.logs.repository.EventLogRepository;
import com.example.account.domain.logs.service.EventLogService;
import com.example.account.util.enums.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventLogServiceTest {

    @InjectMocks
    private EventLogService eventLogService;

    @Mock
    private EventLogRepository eventLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void testSaveMessageToLog() throws JsonProcessingException {
        String jsonMessage = "{\"objectId\":\"test_id\",\"eventType\":\"test_event\"}"; // Replace with your JSON message
        EventLog expectedEventLog = new EventLog(UUID.randomUUID(), EventType.CREATION, UUID.randomUUID());

        when(objectMapper.readValue(jsonMessage, EventLog.class)).thenReturn(expectedEventLog);

        eventLogService.saveMessageToLog(jsonMessage);

        verify(eventLogRepository, times(1)).insert(expectedEventLog);
    }

    @Test(expected = JsonProcessingException.class)
    public void testSaveMessageToLogJsonProcessingException() throws JsonProcessingException {
        String jsonMessage = "{\"objectId\":\"test_id\",\"eventType\":\"test_event\"}";

        when(objectMapper.readValue(jsonMessage, EventLog.class)).thenThrow(JsonProcessingException.class);

        eventLogService.saveMessageToLog(jsonMessage);
    }
}
