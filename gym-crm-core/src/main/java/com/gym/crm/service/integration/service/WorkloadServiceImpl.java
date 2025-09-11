package com.gym.crm.service.integration.service;

import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import com.gym.crm.exception.ServiceUnavailableException;
import com.gym.crm.model.Training;
import com.gym.crm.service.integration.client.WorkloadClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.gym.crm.dto.trainer.TrainerWorkloadRequest.ActionType.DELETE;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl {
    private final WorkloadClient workloadClient;

    @CircuitBreaker(name = "workloadService", fallbackMethod = "fallbackProcessTrainerWorkload")
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        workloadClient.processWorkload(request);
    }

    public void deleteTraineeWorkloads(Set<Training> trainings) {
        if (trainings == null || trainings.isEmpty()) {
            log.info("No trainings to delete from workload service");
            return;
        }

        log.info("Processing deletion of {} trainings from workload service", trainings.size());

        trainings.forEach(training -> {
            try {
                TrainerWorkloadRequest request = buildWorkloadRequest(training);

                processTrainerWorkload(request);

                log.debug("Successfully deleted training {} from workload for trainer {}",
                        training.getId(), training.getTrainer().getUser().getUsername());

            } catch (ServiceUnavailableException e) {
                log.error("Workload service unavailable while deleting training {}: {}", training.getId(), e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Failed to delete training {} from workload service: {}", training.getId(), e.getMessage(), e);
            }
        });
    }

    public void fallbackProcessTrainerWorkload(TrainerWorkloadRequest request, Throwable ex) {
        throw new ServiceUnavailableException("Workload service is currently unavailable. Please try again later.", ex);
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
