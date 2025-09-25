package com.gym.crm.workloadservice.integration;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workloadservice.service.impl.TrainerWorkloadServiceImpl;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static com.gym.crm.openapi.model.TrainerWorkloadRequest.ActionTypeEnum.ADD;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageListenerTest {
    @Mock
    private TrainerWorkloadServiceImpl trainerWorkloadService;
    @InjectMocks
    private WorkloadMessageListener listener;

    @Test
    void shouldProcessMessageAndCallService() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("saitama");
        request.setActionType(ADD);

        Message message = mock(Message.class);
        when(message.getStringProperty("transactionId")).thenReturn("tx-123");
        when(message.getStringProperty("trainerUsername")).thenReturn("saitama");
        when(message.getStringProperty("actionType")).thenReturn("ADD");

        listener.handleTrainerWorkload(request, message);

        verify(trainerWorkloadService).processTrainingWorkload(request);
        assertNull(MDC.get("transactionId"));
    }

    @Test
    void shouldGenerateTransactionIdIfMissing() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        Message message = mock(Message.class);

        when(message.getStringProperty("transactionId")).thenReturn(null);

        listener.handleTrainerWorkload(request, message);

        verify(trainerWorkloadService).processTrainingWorkload(request);
        assertNull(MDC.get("transactionId"));
    }

    @Test
    void shouldHandleJmsExceptionGracefully() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        Message message = mock(Message.class);

        when(message.getStringProperty(anyString())).thenThrow(new JMSException("fail"));

        listener.handleTrainerWorkload(request, message);

        verifyNoInteractions(trainerWorkloadService);
        assertNull(MDC.get("transactionId"));
    }
}