package com.gym.crm.workloadservice.service.impl;

import com.gym.crm.openapi.model.TrainerWorkloadRequest;
import com.gym.crm.openapi.model.TrainerWorkloadResponse;
import com.gym.crm.workloadservice.model.Month;
import com.gym.crm.workloadservice.model.Trainer;
import com.gym.crm.workloadservice.model.Year;
import com.gym.crm.workloadservice.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-testing-purposes-that-is-long-enough"
})
class TrainerWorkloadServiceImplTest {
    @Autowired
    private TrainerWorkloadServiceImpl service;
    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    void shouldAddWorkloadToExistingMonth() {
        createTrainerWithMonth("saitama.punch", "Saitama", "Punch", 8, 120);
        TrainerWorkloadRequest request = buildRequest("saitama.punch", "Saitama", "Punch",
                LocalDate.of(2025, 8, 20), 60, TrainerWorkloadRequest.ActionTypeEnum.ADD);

        service.processTrainingWorkload(request);

        Trainer actual = trainerRepository.findByUsername("saitama.punch").orElseThrow();
        assertThat(actual.getYears().getFirst().getMonths().getFirst().getTotalDurationMinutes()).isEqualTo(180);
    }

    @Test
    void shouldCreateYearAndMonthIfNotExist() {
        TrainerWorkloadRequest request = buildRequest("bruce.lee", "Bruce", "Lee",
                LocalDate.of(2024, 5, 10), 45, TrainerWorkloadRequest.ActionTypeEnum.ADD);

        service.processTrainingWorkload(request);

        Trainer actual = trainerRepository.findByUsername("bruce.lee").orElseThrow();
        assertThat(actual.getYears()).hasSize(1);
        assertThat(actual.getYears().getFirst().getYearNumber()).isEqualTo(2024);
        assertThat(actual.getYears().getFirst().getMonths()).hasSize(1);
        assertThat(actual.getYears().getFirst().getMonths().getFirst().getMonthNumber()).isEqualTo(5);
        assertThat(actual.getYears().getFirst().getMonths().getFirst().getTotalDurationMinutes()).isEqualTo(45);
    }

    @Test
    void shouldNotGoBelowZeroOnDelete() {
        createTrainerWithMonth("sasuke.uchiha", "Sasuke", "Uchiha", 8, 30);
        TrainerWorkloadRequest request = buildRequest("sasuke.uchiha", "Sasuke", "Uchiha",
                LocalDate.of(2025, 8, 1), 100, TrainerWorkloadRequest.ActionTypeEnum.DELETE);

        service.processTrainingWorkload(request);

        Trainer actual = trainerRepository.findByUsername("sasuke.uchiha").orElseThrow();
        assertThat(actual.getYears().getFirst().getMonths().getFirst().getTotalDurationMinutes()).isZero();
    }

    @Test
    void shouldReturnTrainerWorkload() {
        createTrainerWithMonth("ichigo.kurosaki", "Ichigo", "Kurosaki", 5, 90);

        TrainerWorkloadResponse actual = service.getTrainerWorkload("ichigo.kurosaki");

        assertThat(actual.getUsername()).isEqualTo("ichigo.kurosaki");
        assertThat(actual.getYears()).isNotEmpty();
        assertThat(actual.getYears().getFirst().getMonths().getFirst().getTrainingSummaryDuration()).isEqualTo(90);
    }

    private void createTrainerWithMonth(String username, String firstName, String lastName, int monthNumber, int minutes) {
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setIsActive(true);
        trainer.setYears(new ArrayList<>());

        Year year = new Year();
        year.setYearNumber(2025);
        year.setTrainer(trainer);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonthNumber(monthNumber);
        month.setTotalDurationMinutes(minutes);
        month.setYear(year);

        year.getMonths().add(month);
        trainer.getYears().add(year);

        trainerRepository.save(trainer);
    }

    private TrainerWorkloadRequest buildRequest(String username, String firstName, String lastName, LocalDate date, int duration,
                                                TrainerWorkloadRequest.ActionTypeEnum action) {
        return new TrainerWorkloadRequest(username, firstName, lastName, date, duration, true, action);
    }
}