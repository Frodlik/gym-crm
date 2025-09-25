package com.gym.crm.workloadservice.service.impl;

import com.gym.crm.openapi.model.MonthlyWorkload;
import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.openapi.model.TrainerWorkloadResponse;
import com.gym.crm.openapi.model.YearlyWorkload;
import com.gym.crm.workloadservice.mapper.TrainerMapper;
import com.gym.crm.workloadservice.model.Month;
import com.gym.crm.workloadservice.model.Trainer;
import com.gym.crm.workloadservice.model.Year;
import com.gym.crm.workloadservice.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainerMapper trainerMapper;
    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    @Test
    void shouldAddWorkloadToExistingMonth() {
        Trainer existingTrainer = createTrainerWithMonth("saitama.punch", "Saitama", "Punch", 8, 120);
        TrainerWorkloadRequest request = buildRequest("saitama.punch", "Saitama", "Punch",
                LocalDate.of(2025, 8, 20), 60, TrainerWorkloadRequest.ActionTypeEnum.ADD);

        when(trainerRepository.findByUsername("saitama.punch")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        service.processTrainingWorkload(request);

        verify(trainerRepository).save(existingTrainer);
        assertThat(existingTrainer.getYears().getFirst().getMonths().getFirst().getTotalDurationMinutes())
                .isEqualTo(180);
    }

    @Test
    void shouldCreateYearAndMonthIfNotExist() {
        TrainerWorkloadRequest request = buildRequest("bruce.lee", "Bruce", "Lee",
                LocalDate.of(2024, 5, 10), 45, TrainerWorkloadRequest.ActionTypeEnum.ADD);
        Trainer newTrainer = createEmptyTrainer();
        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);

        when(trainerRepository.findByUsername("bruce.lee")).thenReturn(Optional.empty());
        when(trainerMapper.toTrainer(request)).thenReturn(newTrainer);
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.processTrainingWorkload(request);

        verify(trainerMapper).toTrainer(request);
        verify(trainerRepository).save(captor.capture());
        Trainer actual = captor.getValue();
        assertThat(actual.getYears()).hasSize(1);
        assertThat(actual.getYears().getFirst().getYearNumber()).isEqualTo(2024);
        assertThat(actual.getYears().getFirst().getMonths()).hasSize(1);
        assertThat(actual.getYears().getFirst().getMonths().getFirst().getMonthNumber()).isEqualTo(5);
        assertThat(actual.getYears().getFirst().getMonths().getFirst().getTotalDurationMinutes()).isEqualTo(45);
    }

    @Test
    void shouldNotGoBelowZeroOnDelete() {
        Trainer existingTrainer = createTrainerWithMonth("sasuke.uchiha", "Sasuke", "Uchiha", 8, 30);
        TrainerWorkloadRequest request = buildRequest("sasuke.uchiha", "Sasuke", "Uchiha",
                LocalDate.of(2025, 8, 1), 100, TrainerWorkloadRequest.ActionTypeEnum.DELETE);

        when(trainerRepository.findByUsername("sasuke.uchiha")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        service.processTrainingWorkload(request);

        assertThat(existingTrainer.getYears().getFirst().getMonths().getFirst().getTotalDurationMinutes()).isZero();
    }

    @Test
    void shouldReturnTrainerWorkload() {
        Trainer existingTrainer = createTrainerWithMonth("ichigo.kurosaki", "Ichigo", "Kurosaki", 5, 90);
        MonthlyWorkload monthly = new MonthlyWorkload();
        monthly.setMonth(5);
        monthly.setTrainingSummaryDuration(90);

        YearlyWorkload yearly = new YearlyWorkload();
        yearly.setYear(2025);
        yearly.setMonths(List.of(monthly));

        TrainerWorkloadResponse mappedResponse = new TrainerWorkloadResponse();
        mappedResponse.setUsername("ichigo.kurosaki");
        mappedResponse.setYears(List.of(yearly));

        when(trainerRepository.findByUsername("ichigo.kurosaki")).thenReturn(Optional.of(existingTrainer));
        when(trainerMapper.toTrainerWorkloadResponse(existingTrainer)).thenReturn(mappedResponse);

        TrainerWorkloadResponse actual = service.getTrainerWorkload("ichigo.kurosaki");

        assertThat(actual.getUsername()).isEqualTo("ichigo.kurosaki");
        assertThat(actual.getYears()).hasSize(1);
        assertThat(actual.getYears().getFirst().getMonths()).hasSize(1);
        assertThat(actual.getYears().getFirst().getMonths().getFirst().getTrainingSummaryDuration()).isEqualTo(90);
    }

    private Trainer createTrainerWithMonth(String username, String firstName, String lastName, int monthNumber, int minutes) {
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setIsActive(true);
        trainer.setYears(new ArrayList<>());

        Year year = new Year();
        year.setYearNumber(2025);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonthNumber(monthNumber);
        month.setTotalDurationMinutes(minutes);

        year.getMonths().add(month);
        trainer.getYears().add(year);

        return trainer;
    }

    private Trainer createEmptyTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("bruce.lee");
        trainer.setFirstName("Bruce");
        trainer.setLastName("Lee");
        trainer.setIsActive(true);
        trainer.setYears(new ArrayList<>());

        return trainer;
    }

    private TrainerWorkloadRequest buildRequest(String username, String firstName, String lastName, LocalDate date, int duration,
                                                TrainerWorkloadRequest.ActionTypeEnum action) {
        return new TrainerWorkloadRequest(username, firstName, lastName, date, duration, true, action);
    }
}