package com.gym.crm.workloadservice.service.integration;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DLQListener {
    @JmsListener(destination = "${app.queues.trainer-workload-dlq:trainer.workload.dlq}")
    public void handleFailedMessages(Message message) {
        try {
            String trainerUsername = message.getStringProperty("trainerUsername");
            String actionType = message.getStringProperty("actionType");
            String transactionId = message.getStringProperty("transactionId");

            log.error("Message moved to DLQ. Trainer: {}, Action: {}, TransactionId: {}", trainerUsername, actionType, transactionId);

            if (message instanceof TextMessage textMessage) {
                log.error("DLQ payload: {}", textMessage.getText());
            }
        } catch (JMSException e) {
            log.error("Failed to process DLQ message: {}", e.getMessage(), e);
        }
    }
}
