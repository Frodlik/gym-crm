package com.gym.crm.workloadservice.integration;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.workloadservice.service.impl.TrainerWorkloadServiceImpl;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadMessageListener {
    private static final String TRANSACTION_ID = "transactionId";

    private final TrainerWorkloadServiceImpl trainerWorkloadService;

    @JmsListener(destination = "${jms.queues.trainer-workload:trainer.workload.queue}", containerFactory = "jmsListenerContainerFactory")
    public void handleTrainerWorkload(@Payload TrainerWorkloadRequest request, Message message) {
        try {
            String transactionId = extractTransactionId(message);
            MDC.put(TRANSACTION_ID, transactionId);

            String trainerUsername = message.getStringProperty("trainerUsername");
            String actionType = message.getStringProperty("actionType");

            log.info("Processing trainer workload message Trainer: {}, Action: {}", trainerUsername, actionType);

            trainerWorkloadService.processTrainingWorkload(request);

            log.info("Successfully processed workload message for trainer: {} with action: {}", request.getTrainerUsername(), request.getActionType());
        } catch (JMSException e) {
            log.error("Error reading message properties {}", e.getMessage());
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }

    private String extractTransactionId(Message message) throws JMSException {
        String transactionId = message.getStringProperty(TRANSACTION_ID);
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
            log.warn("No transactionId found in JMS message, generated new: {}", transactionId);
        }

        return transactionId;
    }
}
