package com.gym.crm.service.integration.service;

import com.gym.crm.exception.ServiceUnavailableException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.User;
import com.gym.crm.service.integration.client.WorkloadClient;
import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gym.crm.dto.trainer.TrainerWorkloadRequest.ActionType.ADD;
import static com.gym.crm.dto.trainer.TrainerWorkloadRequest.ActionType.DELETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @Test
    void deleteTraineeWorkloads_WhenTrainingsIsNull_ShouldNotCallWorkloadClient() {
        service.deleteTraineeWorkloads(null);

        verify(workloadClient, never()).processWorkload(any());
    }

    @Test
    void deleteTraineeWorkloads_WhenTrainingsIsEmpty_ShouldNotCallWorkloadClient() {
        Set<Training> emptyTrainings = new HashSet<>();

        service.deleteTraineeWorkloads(emptyTrainings);

        verify(workloadClient, never()).processWorkload(any());
    }

    @Test
    void deleteTraineeWorkloads_WithMultipleTrainings_ShouldCallWorkloadClientForEach() {
        Set<Training> trainings = createTrainingsSet(3);

        service.deleteTraineeWorkloads(trainings);

        verify(workloadClient, times(3)).processWorkload(any(TrainerWorkloadRequest.class));

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(workloadClient, times(3)).processWorkload(captor.capture());

        List<TrainerWorkloadRequest> capturedRequests = captor.getAllValues();
        assertThat(capturedRequests).hasSize(3);
        assertThat(capturedRequests).allMatch(req -> req.getActionType() == DELETE);
    }

    @Test
    void deleteTraineeWorkloads_WhenServiceUnavailable_ShouldThrowException() {
        Set<Training> trainings = createTrainingsSet(1);
        ServiceUnavailableException exception = new ServiceUnavailableException("Service unavailable");

        doThrow(exception).when(workloadClient).processWorkload(any());

        assertThrows(ServiceUnavailableException.class,
                () -> service.deleteTraineeWorkloads(trainings));

        verify(workloadClient, times(1)).processWorkload(any());
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

    private Set<Training> createTrainingsSet(int count) {
        Set<Training> trainings = new HashSet<>();

        for (int i = 1; i <= count; i++) {
            User trainerUser = User.builder()
                    .username("trainer" + i)
                    .firstName("Trainer")
                    .lastName("Number" + i)
                    .isActive(true)
                    .build();

            Trainer trainer = Trainer.builder()
                    .id((long) i)
                    .user(trainerUser)
                    .build();

            Training training = Training.builder()
                    .id((long) i)
                    .trainingName("Training " + i)
                    .trainingDate(LocalDate.now().plusDays(i))
                    .trainingDuration(60)
                    .trainer(trainer)
                    .build();

            trainings.add(training);
        }

        return trainings;
    }
}
