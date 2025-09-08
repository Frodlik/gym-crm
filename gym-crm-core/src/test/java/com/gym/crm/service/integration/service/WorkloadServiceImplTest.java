package com.gym.crm.service.integration.service;

import com.gym.crm.service.integration.client.WorkloadClient;
import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.gym.crm.dto.trainer.TrainerWorkloadRequest.ActionType.ADD;
import static com.gym.crm.dto.trainer.TrainerWorkloadRequest.ActionType.DELETE;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceImplTest {
    @Mock
    private WorkloadClient workloadClient;
    @InjectMocks
    private WorkloadServiceImpl service;

    @Test
    void processTrainerWorkload_ShouldCallWorkloadClient() {
        TrainerWorkloadRequest request = createAddWorkloadRequest();

        service.processTrainerWorkload(request);

        verify(workloadClient).processWorkload(request);
    }

    @Test
    void processTrainerWorkload_ShouldPassCorrectRequestToClient() {
        TrainerWorkloadRequest request = createDeleteWorkloadRequest();

        service.processTrainerWorkload(request);

        verify(workloadClient).processWorkload(request);
    }

    private TrainerWorkloadRequest createAddWorkloadRequest() {
        return TrainerWorkloadRequest.builder()
                .trainerUsername("trainer.username")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .isActive(true)
                .actionType(ADD)
                .build();
    }

    private TrainerWorkloadRequest createDeleteWorkloadRequest() {
        return TrainerWorkloadRequest.builder()
                .trainerUsername("mike.johnson")
                .trainerFirstName("Mike")
                .trainerLastName("Johnson")
                .trainingDate(LocalDate.of(2024, 2, 20))
                .trainingDuration(90)
                .isActive(false)
                .actionType(DELETE)
                .build();
    }
}
