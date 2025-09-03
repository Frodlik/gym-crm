package com.gym.crm.workloadservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.workloadservice.model.Trainer;
import com.gym.crm.workloadservice.model.Year;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataSet(value = "datasets/years.yml", cleanBefore = true, cleanAfter = true)
class YearRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private YearRepository repository;
    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    void shouldFindAllYears_Successfully() {
        List<Year> actual = repository.findAll();

        assertThat(actual).hasSize(5);
    }

    public void testFindById_WhenYearExists_ShouldReturnYear() {
        Long yearId = 1L;

        Optional<Year> actual = repository.findById(yearId);

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(yearId);
        assertThat(actual.get().getYearNumber()).isEqualTo(2023);
    }

    @Test
    void shouldReturnEmptyOptional_WhenYearIdDoesNotExist() {
        Long nonExistingYearId = 999L;

        Optional<Year> actual = repository.findById(nonExistingYearId);

        assertThat(actual).isEmpty();
    }

    @Test
    void testSave_WhenCreateNewYear_ShouldPersistYear() {
        Trainer trainer = trainerRepository.findById(3L).orElseThrow();
        Year newYear = Year.builder()
                .yearNumber(2025)
                .trainer(trainer)
                .build();

        Year actual = repository.save(newYear);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getYearNumber()).isEqualTo(2025);
        assertThat(actual.getTrainer().getId()).isEqualTo(3L);
    }

    @Test
    void testSave_WhenUpdateExistingYear_ShouldUpdateYear() {
        Year existingYear = repository.findById(2L).orElseThrow();
        existingYear.setYearNumber(2025);

        Year actual = repository.save(existingYear);

        assertThat(actual.getYearNumber()).isEqualTo(2025);
        assertThat(actual.getId()).isEqualTo(2L);
    }

    @Test
    void testDelete_WhenYearExists_ShouldDeleteYear() {
        Year yearToDelete = repository.findById(5L).orElseThrow();

        repository.delete(yearToDelete);

        Optional<Year> actual = repository.findById(5L);
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldCountTotalYears_Correctly() {
        long actual = repository.count();

        assertThat(actual).isEqualTo(5L);
    }
}