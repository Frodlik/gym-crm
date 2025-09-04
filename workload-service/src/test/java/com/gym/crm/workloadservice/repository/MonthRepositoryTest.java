package com.gym.crm.workloadservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.workloadservice.model.Month;
import com.gym.crm.workloadservice.model.Trainer;
import com.gym.crm.workloadservice.model.Year;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "datasets/months.yml", cleanBefore = true, cleanAfter = true)
class MonthRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private MonthRepository repository;

    @Test
    void shouldFindAllMonths_Successfully() {
        List<Month> actual = repository.findAll();

        assertThat(actual).hasSize(4);
    }

    @Test
    void testFindById_WhenMonthExists_ShouldReturnMonth() {
        Long monthId = 2L;

        Optional<Month> actual = repository.findById(monthId);

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(monthId);
        assertThat(actual.get().getMonthNumber()).isEqualTo(1);
        assertThat(actual.get().getTotalDurationMinutes()).isEqualTo(120);
    }

    @Test
    void shouldReturnEmptyOptional_WhenMonthIdDoesNotExist() {
        Long nonExistingMonthId = 999L;

        Optional<Month> actual = repository.findById(nonExistingMonthId);

        assertThat(actual).isEmpty();
    }

    @Test
    void testSave_WhenCreateNewMonth_ShouldPersistMonth() {
        Year year = createYear();
        Month newMonth = Month.builder()
                .monthNumber(12)
                .totalDurationMinutes(180)
                .year(year)
                .build();

        Month actual = repository.save(newMonth);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getMonthNumber()).isEqualTo(12);
        assertThat(actual.getTotalDurationMinutes()).isEqualTo(180);
    }

    @Test
    void testSave_WhenUpdateExistingMonth_ShouldUpdateMonth() {
        Month existingMonth = repository.findById(2L).orElseThrow();
        existingMonth.setTotalDurationMinutes(250);

        Month actual = repository.save(existingMonth);

        assertThat(actual.getTotalDurationMinutes()).isEqualTo(250);
        assertThat(actual.getId()).isEqualTo(2L);
        assertThat(actual.getMonthNumber()).isEqualTo(1);
    }

    @Test
    void testDelete_WhenMonthExists_ShouldDeleteMonth() {
        Month monthToDelete = repository.findById(3L).orElseThrow();

        repository.delete(monthToDelete);

        Optional<Month> actual = repository.findById(3L);
        assertThat(actual).isEmpty();
    }

    @Test
    void testDeleteById_WhenMonthExists_ShouldDeleteMonth() {
        Long monthIdToDelete = 5L;

        repository.deleteById(monthIdToDelete);

        assertThat(repository.findById(monthIdToDelete)).isEmpty();
    }

    @Test
    void shouldCountTotalMonths_Correctly() {
        long actual = repository.count();

        assertThat(actual).isEqualTo(4L);
    }

    private Trainer createTrainer() {
        return Trainer.builder()
                .username("puck.rubick")
                .firstName("puck")
                .lastName("rubick")
                .isActive(true)
                .build();
    }

    private Year createYear() {
        return Year.builder()
                .yearNumber(2024)
                .trainer(createTrainer())
                .build();
    }
}