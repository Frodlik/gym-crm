package com.gym.crm.service.integration.service;

import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import com.gym.crm.exception.JmsMessageException;
import com.gym.crm.exception.ServiceUnavailableException;
import com.gym.crm.model.Training;
import com.gym.crm.util.QueueProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static com.gym.crm.dto.trainer.TrainerWorkloadRequest.ActionType.DELETE;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl {
    private static final String TRANSACTION_ID = "transactionId";

    private final JmsTemplate jmsTemplate;
    private final QueueProperties queueProperties;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallbackProcessTrainerWorkload")
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        try {
            String transactionId = getTransactionId();
            log.info("Sending trainer workload message for trainer: {}", request.getTrainerUsername());

            jmsTemplate.convertAndSend(queueProperties.getTrainerWorkloadQueue(), request, message -> {
                message.setStringProperty(TRANSACTION_ID, transactionId);
                message.setStringProperty("trainerUsername", request.getTrainerUsername());
                message.setStringProperty("actionType", request.getActionType().toString());

                return message;
            });

            log.debug("Successfully sent workload message for trainer: {} with action: {}", request.getTrainerUsername(), request.getActionType());
        } catch (JmsException e) {
            throw new JmsMessageException("Failed to send JMS message for trainer workload", e);
        }
    }

    public void deleteTraineeWorkloads(Set<Training> trainings) {
        if (trainings == null || trainings.isEmpty()) {
            log.info("No trainings to delete from workload service");
            return;
        }

        log.info("Processing deletion of {} trainings from workload service", trainings.size());

        trainings.forEach(this::deleteTrainingFromWorkload);
    }

    public void fallbackProcessTrainerWorkload(TrainerWorkloadRequest request, Throwable ex) {
        throw new ServiceUnavailableException("Workload service is currently unavailable. Please try again later.", ex);
    }

    private String getTransactionId() {
        String transactionId = MDC.get(TRANSACTION_ID);
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
            MDC.put(TRANSACTION_ID, transactionId);
        }

        return transactionId;
    }

    private void deleteTrainingFromWorkload(Training training) {
        try {
            TrainerWorkloadRequest request = buildWorkloadRequest(training);
            processTrainerWorkload(request);

            log.debug("Successfully deleted training {} from workload for trainer {}",
                    training.getId(), training.getTrainer().getUser().getUsername());
        } catch (ServiceUnavailableException e) {
            log.error("Workload service unavailable while deleting training {}: {}", training.getId(), e.getMessage());
            throw e;
        }
    }

    private TrainerWorkloadRequest buildWorkloadRequest(Training training) {
        return TrainerWorkloadRequest.builder()
                .trainerUsername(training.getTrainer().getUser().getUsername())
                .trainerFirstName(training.getTrainer().getUser().getFirstName())
                .trainerLastName(training.getTrainer().getUser().getLastName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .isActive(training.getTrainer().getUser().getIsActive())
                .actionType(DELETE)
                .build();
    }
}
