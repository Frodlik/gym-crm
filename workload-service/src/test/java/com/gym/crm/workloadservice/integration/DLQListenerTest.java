package com.gym.crm.workloadservice.integration;

import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DLQListenerTest {
    @Mock
    private TextMessage textMessage;
    @InjectMocks
    private DLQListener dlqListener;

    @Test
    void handleFailedMessages_ShouldLogMessageDetails() throws Exception {
        when(textMessage.getStringProperty("trainerUsername")).thenReturn("trainer1");
        when(textMessage.getStringProperty("actionType")).thenReturn("ADD");
        when(textMessage.getStringProperty("transactionId")).thenReturn("tx-123");
        when(textMessage.getText()).thenReturn("{ \"trainer\": \"trainer1\" }");

        dlqListener.handleFailedMessages(textMessage);

        verify(textMessage).getStringProperty("trainerUsername");
        verify(textMessage).getStringProperty("actionType");
        verify(textMessage).getStringProperty("transactionId");
        verify(textMessage).getText();
    }

    @Test
    void handleFailedMessages_WhenJmsExceptionThrown_ShouldCatchAndLog() throws Exception {
        when(textMessage.getStringProperty("trainerUsername")).thenThrow(new JMSException("Test JMS error"));

        dlqListener.handleFailedMessages(textMessage);

        verify(textMessage).getStringProperty("trainerUsername");
    }
}