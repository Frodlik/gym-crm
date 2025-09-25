package com.gym.crm.workloadservice.repository;

import com.gym.crm.workloadservice.model.Trainer;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class TrainerRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private TrainerRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @BeforeEach
    void setUp() {
        List<Trainer> testTrainers = createTestTrainers();
        repository.saveAll(testTrainers);
    }

    @Test
    void testFindById_WhenTrainerExists_ShouldReturnTrainer() {
        Trainer expectedTrainer = repository.findByUsername("son.goku").orElseThrow();
        String trainerId = expectedTrainer.getId();

        Optional<Trainer> actual = repository.findById(trainerId);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(trainer -> {
                    assertThat(trainer.getId()).isEqualTo(trainerId);
                    assertThat(trainer.getUsername()).isEqualTo("son.goku");
                    assertThat(trainer.getFirstName()).isEqualTo("Son");
                    assertThat(trainer.getLastName()).isEqualTo("Goku");
                    assertThat(trainer.getIsActive()).isTrue();
                });
    }

    @Test
    void testFindById_WhenTrainerNotExists_ShouldReturnEmpty() {
        String nonExistentId = new ObjectId().toString();

        Optional<Trainer> actual = repository.findById(nonExistentId);

        assertThat(actual).isEmpty();
    }

    @Test
    void testFindByUsername_WhenTrainerExists_ShouldReturnTrainer() {
        String username = "vegeta.prince";

        Optional<Trainer> actual = repository.findByUsername(username);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(trainer -> {
                    assertThat(trainer.getUsername()).isEqualTo(username);
                    assertThat(trainer.getFirstName()).isEqualTo("Vegeta");
                    assertThat(trainer.getLastName()).isEqualTo("Prince");
                    assertThat(trainer.getIsActive()).isTrue();
                });
    }

    @Test
    void testFindByUsername_WhenTrainerNotExists_ShouldReturnEmpty() {
        String nonExistentUsername = "non.existent.user";

        Optional<Trainer> actual = repository.findByUsername(nonExistentUsername);

        assertThat(actual).isEmpty();
    }

    @Test
    void testSave_WhenCreateNewTrainer_ShouldPersistTrainer() {
        Trainer newTrainer = createTrainer("rayan.goslin", "Ryan", "Gosling");

        Trainer actual = repository.save(newTrainer);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUsername()).isEqualTo("rayan.goslin");
        assertThat(actual.getFirstName()).isEqualTo("Ryan");
        assertThat(actual.getLastName()).isEqualTo("Gosling");
        assertThat(actual.getIsActive()).isTrue();
        Optional<Trainer> retrievedTrainer = repository.findById(actual.getId());
        assertThat(retrievedTrainer).isPresent();
    }

    @Test
    void testSave_WhenUpdateExistingTrainer_ShouldUpdateTrainer() {
        Trainer existingTrainer = repository.findByUsername("son.goku").orElseThrow();
        String originalId = existingTrainer.getId();
        existingTrainer.setIsActive(false);
        existingTrainer.setFirstName("Updated Son");

        Trainer actual = repository.save(existingTrainer);

        assertThat(actual.getId()).isEqualTo(originalId);
        assertThat(actual.getIsActive()).isFalse();
        assertThat(actual.getFirstName()).isEqualTo("Updated Son");
        Trainer retrievedTrainer = repository.findById(originalId).orElseThrow();
        assertThat(retrievedTrainer.getIsActive()).isFalse();
        assertThat(retrievedTrainer.getFirstName()).isEqualTo("Updated Son");
    }

    @Test
    void testDelete_WhenTrainerExists_ShouldDeleteTrainer() {
        Trainer trainerToDelete = repository.findByUsername("naruto.uzumaki").orElseThrow();
        String trainerId = trainerToDelete.getId();
        long initialCount = repository.count();

        repository.delete(trainerToDelete);

        assertThat(repository.findById(trainerId)).isEmpty();
        assertThat(repository.count()).isEqualTo(initialCount - 1);
    }

    @Test
    void testDeleteById_WhenTrainerExists_ShouldDeleteTrainer() {
        Trainer trainer = repository.findByUsername("vegeta.prince").orElseThrow();
        String trainerIdToDelete = trainer.getId();
        long initialCount = repository.count();

        repository.deleteById(trainerIdToDelete);

        assertThat(repository.findById(trainerIdToDelete)).isEmpty();
        assertThat(repository.count()).isEqualTo(initialCount - 1);
    }

    @Test
    void testFindAll_ShouldReturnAllTrainers() {
        List<Trainer> actual = repository.findAll();

        assertThat(actual).hasSize(3);
        assertThat(actual)
                .extracting(Trainer::getUsername)
                .containsExactlyInAnyOrder("son.goku", "vegeta.prince", "naruto.uzumaki");
    }

    @Test
    void testCount_ShouldReturnCorrectCount() {
        long actual = repository.count();

        assertThat(actual).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(strings = {"son.goku", "vegeta.prince", "naruto.uzumaki"})
    void testExistsByUsername_WhenTrainerExists_ShouldReturnTrue(String username) {
        boolean exists = repository.findByUsername(username).isPresent();

        assertThat(exists).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"nonexistent.user", "fake.trainer", ""})
    void testExistsByUsername_WhenTrainerNotExists_ShouldReturnFalse(String username) {
        boolean exists = repository.findByUsername(username).isPresent();

        assertThat(exists).isFalse();
    }

    private List<Trainer> createTestTrainers() {
        return List.of(
                createTrainer("son.goku", "Son", "Goku"),
                createTrainer("vegeta.prince", "Vegeta", "Prince"),
                createTrainer("naruto.uzumaki", "Naruto", "Uzumaki")
        );
    }

    private Trainer createTrainer(String username, String firstName, String lastName) {
        return Trainer.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .years(new ArrayList<>())
                .build();
    }
}