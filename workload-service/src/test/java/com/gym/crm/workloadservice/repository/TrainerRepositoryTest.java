package com.gym.crm.workloadservice.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.workloadservice.model.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataSet(value = "datasets/trainers.yml", cleanBefore = true, cleanAfter = true)
class TrainerRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private TrainerRepository repository;

    @Test
    void testFindById_WhenTrainerExists_ShouldReturnTrainer() {
        Long trainerId = 2L;

        Optional<Trainer> actual = repository.findById(trainerId);

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(trainerId);
        assertThat(actual.get().getUsername()).isEqualTo("son.goku");
        assertThat(actual.get().getFirstName()).isEqualTo("Son");
        assertThat(actual.get().getLastName()).isEqualTo("Goku");
        assertThat(actual.get().getIsActive()).isTrue();
    }

    @Test
    void testFindByUsername_WhenTrainerExists_ShouldReturnTrainer() {
        String username = "vegeta.prince";

        Optional<Trainer> actual = repository.findByUsername(username);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUsername()).isEqualTo(username);
        assertThat(actual.get().getFirstName()).isEqualTo("Vegeta");
        assertThat(actual.get().getLastName()).isEqualTo("Prince");
        assertThat(actual.get().getIsActive()).isTrue();
    }

    @Test
    void testFindByUsername_WhenTrainerNotExists_ShouldReturnEmpty() {
        String nonExistentUsername = "naruto.uzumaki222431242";

        Optional<Trainer> actual = repository.findByUsername(nonExistentUsername);

        assertThat(actual).isEmpty();
    }

    @Test
    void testSave_WhenCreateNewTrainer_ShouldPersistTrainer() {
        Trainer newTrainer = Trainer.builder()
                .username("rayan.goslin")
                .firstName("Rayan")
                .lastName("Goslin")
                .isActive(true)
                .build();

        Trainer actual = repository.save(newTrainer);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUsername()).isEqualTo("rayan.goslin");
        assertThat(actual.getFirstName()).isEqualTo("Rayan");
        assertThat(actual.getLastName()).isEqualTo("Goslin");
        assertThat(actual.getIsActive()).isTrue();
    }

    @Test
    void testSave_WhenUpdateExistingTrainer_ShouldUpdateTrainer() {
        Trainer existingTrainer = repository.findById(2L).orElseThrow();
        existingTrainer.setIsActive(true);
        existingTrainer.setFirstName("Updated");

        Trainer actual = repository.save(existingTrainer);

        assertThat(actual.getIsActive()).isTrue();
        assertThat(actual.getFirstName()).isEqualTo("Updated");
        assertThat(actual.getId()).isEqualTo(2L);
    }

    @Test
    void testDelete_WhenTrainerExists_ShouldDeleteTrainer() {
        Trainer trainerToDelete = repository.findById(4L).orElseThrow();

        repository.delete(trainerToDelete);

        Optional<Trainer> actual = repository.findById(4L);
        assertThat(actual).isEmpty();
    }

    @Test
    void testDeleteById_WhenTrainerExists_ShouldDeleteTrainer() {
        Long trainerIdToDelete = 3L;

        repository.deleteById(trainerIdToDelete);

        assertThat(repository.findById(trainerIdToDelete)).isEmpty();
    }
}