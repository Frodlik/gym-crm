package com.gym.crm.service.integration.service;

import com.gym.crm.dto.trainer.TrainerWorkloadRequest;
import com.gym.crm.exception.JmsMessageException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.User;
import com.gym.crm.util.QueueProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.JmsException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gym.crm.dto.trainer.TrainerWorkloadRequest.ActionType.ADD;
import static com.gym.crm.dto.trainer.TrainerWorkloadRequest.ActionType.DELETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceImplTest {
    private static final String TRAINER_WORKLOAD_QUEUE = "trainer.workload.queue";
    private static final String TRANSACTION_ID = "test-transaction-id";

    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private QueueProperties queueProperties;
    @InjectMocks
    private WorkloadServiceImpl service;

    @BeforeEach
    void setUp() {
        when(queueProperties.getTrainerWorkloadQueue()).thenReturn(TRAINER_WORKLOAD_QUEUE);
        MDC.clear();
    }

    @Test
    void processTrainerWorkload_ShouldCallJmsTemplate() {
        TrainerWorkloadRequest request = createAddWorkloadRequest();

        service.processTrainerWorkload(request);

        verify(jmsTemplate).convertAndSend(eq(TRAINER_WORKLOAD_QUEUE), eq(request), any(MessagePostProcessor.class));
    }

    @Test
    void processTrainerWorkload_ShouldPassCorrectRequestToJmsTemplate() {
        TrainerWorkloadRequest request = createDeleteWorkloadRequest();

        service.processTrainerWorkload(request);

        verify(jmsTemplate).convertAndSend(eq(TRAINER_WORKLOAD_QUEUE), eq(request), any(MessagePostProcessor.class));
    }

    @Test
    void processTrainerWorkload_WhenJmsExceptionOccurs_ShouldThrowJmsMessageException() {
        TrainerWorkloadRequest request = createAddWorkloadRequest();
        JmsException jmsException = new UncategorizedJmsException("JMS error");

        doThrow(jmsException).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        JmsMessageException exception = assertThrows(JmsMessageException.class,
                () -> service.processTrainerWorkload(request));

        assertThat(exception.getMessage()).contains("Failed to send JMS message for trainer workload");
        assertThat(exception.getCause()).isEqualTo(jmsException);
    }

    @Test
    void processTrainerWorkload_ShouldUseExistingTransactionIdFromMDC() {
        MDC.put("transactionId", TRANSACTION_ID);
        TrainerWorkloadRequest request = createAddWorkloadRequest();

        service.processTrainerWorkload(request);

        verify(jmsTemplate).convertAndSend(eq(TRAINER_WORKLOAD_QUEUE), eq(request), any(MessagePostProcessor.class));
    }

    @Test
    void processTrainerWorkload_ShouldGenerateNewTransactionIdWhenMDCIsEmpty() {
        TrainerWorkloadRequest request = createAddWorkloadRequest();

        service.processTrainerWorkload(request);

        assertThat(MDC.get("transactionId")).isNotNull();
        verify(jmsTemplate).convertAndSend(eq(TRAINER_WORKLOAD_QUEUE), eq(request), any(MessagePostProcessor.class));
    }

    @Test
    void deleteTraineeWorkloads_WithMultipleTrainings_ShouldCallJmsTemplateForEach() {
        Set<Training> trainings = createTrainingsSet(3);

        service.deleteTraineeWorkloads(trainings);

        verify(jmsTemplate, times(3)).convertAndSend(eq(TRAINER_WORKLOAD_QUEUE), any(TrainerWorkloadRequest.class), any(MessagePostProcessor.class));
        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(jmsTemplate, times(3)).convertAndSend(eq(TRAINER_WORKLOAD_QUEUE), captor.capture(), any(MessagePostProcessor.class));
        List<TrainerWorkloadRequest> capturedRequests = captor.getAllValues();
        assertThat(capturedRequests).hasSize(3);
        assertThat(capturedRequests).allMatch(req -> req.getActionType() == DELETE);
        assertThat(capturedRequests).allMatch(req -> req.getTrainerUsername().startsWith("trainer"));
    }

    @Test
    void deleteTraineeWorkloads_WhenServiceUnavailable_ShouldThrowException() {
        Set<Training> trainings = createTrainingsSet(1);
        JmsException jmsException = new UncategorizedJmsException("JMS error");

        doThrow(jmsException).when(jmsTemplate).convertAndSend(anyString(), any(), any(MessagePostProcessor.class));

        assertThrows(JmsMessageException.class,
                () -> service.deleteTraineeWorkloads(trainings));
        verify(jmsTemplate, times(1)).convertAndSend(eq(TRAINER_WORKLOAD_QUEUE), any(TrainerWorkloadRequest.class), any(MessagePostProcessor.class));
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
