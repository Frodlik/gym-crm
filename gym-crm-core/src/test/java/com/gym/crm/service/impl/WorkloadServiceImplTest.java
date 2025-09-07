package com.gym.crm.service.impl;

import com.gym.crm.client.WorkloadClient;
import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceImplTest {
    @Mock
    private WorkloadClient workloadClient;
    @InjectMocks
    private WorkloadServiceImpl service;

    @Test
    void processTrainerWorkload_ShouldCallWorkloadClient() {
        TrainerWorkloadRequest request = buildTrainerWorkloadRequest("trainer.username", "John", "Doe",
                LocalDate.of(2024, 1, 15), 60, true, TrainerWorkloadRequest.ActionType.ADD);

        service.processTrainerWorkload(request);

        verify(workloadClient).processWorkload(request);
    }

    @Test
    void processTrainerWorkload_ShouldPassCorrectRequestToClient() {
        TrainerWorkloadRequest request = buildTrainerWorkloadRequest("mike.johnson", "Mike", "Johnson",
                LocalDate.of(2024, 2, 20), 90, false, TrainerWorkloadRequest.ActionType.DELETE);

        service.processTrainerWorkload(request);

        verify(workloadClient).processWorkload(request);
    }

    private TrainerWorkloadRequest buildTrainerWorkloadRequest(String username, String firstName, String lastName,
            LocalDate trainingDate, int duration, boolean isActive, TrainerWorkloadRequest.ActionType actionType) {
        return TrainerWorkloadRequest.builder()
                .trainerUsername(username)
                .trainerFirstName(firstName)
                .trainerLastName(lastName)
                .trainingDate(trainingDate)
                .trainingDuration(duration)
                .isActive(isActive)
                .actionType(actionType)
                .build();
    }
}
