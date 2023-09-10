package com.example.account.unit;

import com.example.account.domain.logs.entity.EventLog;
import com.example.account.domain.logs.repository.EventLogRepository;
import com.example.account.domain.logs.service.EventLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
        // Arrange
        String jsonMessage = "{\"field1\":\"value1\",\"field2\":\"value2\"}"; // Replace with your JSON message
        EventLog expectedEventLog = new EventLog(); // Create an expected event log instance

        when(objectMapper.readValue(jsonMessage, EventLog.class)).thenReturn(expectedEventLog);

        // Act
        eventLogService.saveMessageToLog(jsonMessage);

        // Assert
        verify(eventLogRepository, times(1)).insert(expectedEventLog);
    }

    @Test(expected = JsonProcessingException.class)
    public void testSaveMessageToLogJsonProcessingException() throws JsonProcessingException {
        // Arrange
        String jsonMessage = "{\"field1\":\"value1\",\"field2\":\"value2\"}"; // Replace with your JSON message

        when(objectMapper.readValue(jsonMessage, EventLog.class)).thenThrow(JsonProcessingException.class);

        // Act and Assert
        eventLogService.saveMessageToLog(jsonMessage);
    }
}
