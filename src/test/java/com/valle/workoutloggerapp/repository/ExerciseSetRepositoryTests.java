package com.valle.workoutloggerapp.repository;

import com.valle.workoutloggerapp.domain.CreateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.domain.entity.ExerciseSet;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.service.ExerciseSetService;
import com.valle.workoutloggerapp.service.impl.ExerciseSetServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.valle.workoutloggerapp.domain.entity.ExerciseName.LAT_PULLDOWN;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(ExerciseSetServiceImpl.class)
public class ExerciseSetRepositoryTests {

    @Autowired
    private ExerciseSetRepository exerciseSetRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseSetService exerciseSetService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    public void exerciseSetRepository_save_GeneratesCreationTimestamp() {
        Exercise exercise = saveExercise();
        ExerciseSet saved = exerciseSetRepository.saveAndFlush(new ExerciseSet(67, 6, 7, exercise));
        entityManager.clear();

        ExerciseSet reloaded = exerciseSetRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(reloaded.getCreatedAt());
    }

    /**
     * Checks that concurrent set creation saves distinct sets with creation timestamps.
     * Test written using AI; documentation updated using AI.
     */
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createExerciseSet_concurrentTransactions_PersistDistinctSets() throws Exception {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);

        // Commit setup before starting workers: they use their own transactions.
        Exercise exercise = transaction.execute(status -> {
            Workout workout = workoutRepository.save(Workout.builder()
                    .title("Concurrent sets")
                    .workoutDate(LocalDate.of(2026, 10, 6))
                    .build());
            return exerciseRepository.save(Exercise.builder()
                    .exerciseName(LAT_PULLDOWN)
                    .workout(workout)
                    .build());
        });
        assertNotNull(exercise);
        var workoutId = exercise.getWorkout().getId();
        var exerciseId = exercise.getId();
        var request = new CreateExerciseSetRequest(67, 6, 7);

        CountDownLatch firstSetCreated = new CountDownLatch(1);
        CountDownLatch allowFirstCommit = new CountDownLatch(1);
        CountDownLatch secondTransactionStarted = new CountDownLatch(1);
        CountDownLatch secondSetCreated = new CountDownLatch(1);
        var workers = Executors.newFixedThreadPool(2);

        try {
            var first = workers.submit(() -> transaction.execute(status -> {
                ExerciseSet set = exerciseSetService.createExerciseSet(workoutId, exerciseId, request);
                firstSetCreated.countDown();
                // The service joins this transaction, so its lock remains held here.
                awaitLatch(allowFirstCommit);
                return set.getId();
            }));
            awaitLatch(firstSetCreated);

            var second = workers.submit(() -> transaction.execute(status -> {
                secondTransactionStarted.countDown();
                ExerciseSet set = exerciseSetService.createExerciseSet(workoutId, exerciseId, request);
                // Signal when the service has saved the second set.
                secondSetCreated.countDown();
                return set.getId();
            }));
            awaitLatch(secondTransactionStarted);

            assertFalse(secondSetCreated.await(500, TimeUnit.MILLISECONDS),
                    "The second request must wait while the first transaction holds the exercise lock");

            allowFirstCommit.countDown();
            UUID firstId = first.get(10, TimeUnit.SECONDS);
            UUID secondId = second.get(10, TimeUnit.SECONDS);
            assertNotEquals(firstId, secondId);

            List<ExerciseSet> savedSets = transaction.execute(status -> exerciseSetRepository
                    .findByExercise_Workout_IdAndExercise_IdOrderByCreatedAtAscIdAsc(workoutId, exerciseId));
            assertNotNull(savedSets);
            assertEquals(2, savedSets.size());
            assertTrue(savedSets.stream().allMatch(set -> set.getCreatedAt() != null));
            assertTrue(savedSets.stream().map(ExerciseSet::getId).toList().containsAll(List.of(firstId, secondId)));
        } finally {
            allowFirstCommit.countDown();
            workers.shutdownNow();
            assertTrue(workers.awaitTermination(10, TimeUnit.SECONDS), "Workers must finish before cleanup");
            // This test commits transactions, so clean up explicitly instead of relying on rollback.
            transaction.executeWithoutResult(status -> workoutRepository.deleteById(workoutId));
        }
    }

    private static void awaitLatch(CountDownLatch latch) {
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), "Timed out waiting for the other transaction");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while coordinating transactions", exception);
        }
    }

    /**
     * Checks that the requested exercise sets are returned in ascending creation-time order with ID breaking ties.
     * Test assertions and documentation updated using AI.
     */
    @Test
    public void exerciseSetRepositoryfindByExercise_Workout_IdAndExercise_IdOrderByCreatedAtAscIdAsc_returnsList_() {
        Workout workout = Workout.builder()
                .title("Back workout")
                .workoutDate(LocalDate.of(2026, 10, 6))
                .workoutSummary("Lat pulldown sets")
                .build();
        workoutRepository.save(workout);

        Exercise exercise = Exercise.builder()
                .exerciseName(LAT_PULLDOWN)
                .workout(workout)
                .build();
        exerciseRepository.save(exercise);

        ExerciseSet set1 = ExerciseSet.builder()
                .weight(40)
                .reps(12)
                .rpe(6)
                .exercise(exercise)
                .build();

        ExerciseSet set2 = ExerciseSet.builder()
                .weight(50)
                .reps(10)
                .rpe(7)
                .exercise(exercise)
                .build();

        ExerciseSet set3 = ExerciseSet.builder()
                .weight(60)
                .reps(8)
                .rpe(8)
                .exercise(exercise)
                .build();

        ExerciseSet set4 = ExerciseSet.builder()
                .weight(65)
                .reps(6)
                .rpe(9)
                .exercise(exercise)
                .build();

        exerciseSetRepository.saveAllAndFlush(List.of(set1, set2, set3, set4));

        Instant earlier = Instant.parse("2026-11-25T10:00:00Z");
        Instant later = earlier.plusSeconds(60);
        setCreationTime(set1, later);
        setCreationTime(set2, earlier);
        setCreationTime(set3, earlier);
        setCreationTime(set4, later.plusSeconds(60));
        entityManager.clear();

        List<ExerciseSet> exerciseSets = exerciseSetRepository
                .findByExercise_Workout_IdAndExercise_IdOrderByCreatedAtAscIdAsc(workout.getId(), exercise.getId());

        List<UUID> tiedIds = java.util.stream.Stream.of(set2.getId(), set3.getId())
                .sorted(java.util.Comparator.comparing(UUID::toString)).toList();
        List<UUID> expectedIds = List.of(tiedIds.get(0), tiedIds.get(1), set1.getId(), set4.getId());
        assertEquals(expectedIds, exerciseSets.stream().map(ExerciseSet::getId).toList());
        Exercise reloadedExercise = exerciseRepository.findById(exercise.getId()).orElseThrow();
        assertEquals(expectedIds, reloadedExercise.getExerciseSets().stream().map(ExerciseSet::getId).toList());
        assertTrue(exerciseSets.stream().allMatch(set ->
                set.getExercise().getId().equals(exercise.getId())));
    }

    /**
     * Checks that the workout, exercise, and set IDs retrieve the expected set and its values.
     * Test assertions and documentation updated using AI.
     */
    @Test
    public void exerciseSetRepository_findByExercise_Workout_IdAndExercise_IdAndId_retunsExerciseSet() {
        Workout workout = Workout.builder()
                .title("Back workout")
                .workoutDate(LocalDate.of(2026, 10, 6))
                .workoutSummary("Lat pulldown sets")
                .build();
        UUID savedWorkoutId = workoutRepository.save(workout).getId();

        Exercise exercise = Exercise.builder()
                .exerciseName(LAT_PULLDOWN)
                .workout(workout)
                .build();
        UUID savedExerciseID = exerciseRepository.save(exercise).getId();

        ExerciseSet set1 = ExerciseSet.builder()
                .weight(40)
                .reps(12)
                .rpe(6)
                .exercise(exercise)
                .build();

        UUID savedExerciseSetId = exerciseSetRepository.save(set1).getId();

        Optional<ExerciseSet> exerciseSet = exerciseSetRepository.
                findByExercise_Workout_IdAndExercise_IdAndId(savedWorkoutId, savedExerciseID, savedExerciseSetId);


        ExerciseSet found = exerciseSet.orElseThrow();
        assertEquals(savedExerciseSetId, found.getId());
        assertEquals(savedExerciseID, found.getExercise().getId());
        assertNotNull(found.getCreatedAt());
        assertEquals(40, found.getWeight());
        assertEquals(12, found.getReps());
        assertEquals(6, found.getRpe());
    }

    /**
     * Checks that deleting a set using its workout, exercise, and set IDs removes it.
     * Test documentation written using AI.
     */
    @Test
    public void exerciseSetRepository_deleteByExercise_Workout_IdAndExercise_IdAndId_returnsEmptyOptional() {
        Workout workout = Workout.builder()
                .title("Back workout")
                .workoutDate(LocalDate.of(2026, 10, 6))
                .workoutSummary("Lat pulldown sets")
                .build();
        UUID savedWorkoutId = workoutRepository.save(workout).getId();

        Exercise exercise = Exercise.builder()
                .exerciseName(LAT_PULLDOWN)
                .workout(workout)
                .build();
        UUID savedExerciseID = exerciseRepository.save(exercise).getId();

        ExerciseSet set1 = ExerciseSet.builder()
                .weight(40)
                .reps(12)
                .rpe(6)
                .exercise(exercise)
                .build();

        UUID savedExerciseSetId = exerciseSetRepository.save(set1).getId();


        exerciseSetRepository.deleteByExercise_Workout_IdAndExercise_IdAndId(
                savedWorkoutId, savedExerciseID, savedExerciseSetId);


        Optional<ExerciseSet> exerciseSet = exerciseSetRepository.
                findByExercise_Workout_IdAndExercise_IdAndId(savedWorkoutId, savedExerciseID, savedExerciseSetId);

        assertTrue(exerciseSet.isEmpty());
    }

    @Autowired
    private EntityManager entityManager;

    private Exercise saveExercise() {
        Workout workout = workoutRepository.save(Workout.builder()
                .title("Back workout")
                .workoutDate(LocalDate.of(2026, 10, 6)).build());
        return exerciseRepository.saveAndFlush(Exercise.builder()
                .exerciseName(LAT_PULLDOWN).workout(workout).build());
    }

    /**
     * Checks that a saved set reloads with its generated ID, recorded values, and exercise association.
     * Test written using AI.
     */
    @Test
    public void exerciseSetRepository_save_PersistsSetAndExercise() {
        Exercise exercise = saveExercise();
        ExerciseSet saved = exerciseSetRepository.saveAndFlush(
                new ExerciseSet(40, 12, 6, exercise));
        entityManager.clear();

        ExerciseSet reloaded = exerciseSetRepository.findById(saved.getId()).orElseThrow();

        assertNotNull(reloaded.getId());
        assertNotNull(reloaded.getCreatedAt());
        assertEquals(40, reloaded.getWeight());
        assertEquals(12, reloaded.getReps());
        assertEquals(6, reloaded.getRpe());
        assertEquals(exercise.getId(), reloaded.getExercise().getId());
    }

    /**
     * Checks that updated weight, reps, and RPE persist while the creation timestamp and exercise remain unchanged.
     * Test written using AI.
     */
    @Test
    public void exerciseSetRepository_saveExistingSet_PersistsUpdates() {
        Exercise exercise = saveExercise();
        UUID workoutId = exercise.getWorkout().getId();
        ExerciseSet saved = exerciseSetRepository.saveAndFlush(
                new ExerciseSet(40, 12, 6, exercise));
        entityManager.clear();

        ExerciseSet updated = exerciseSetRepository
                .findByExercise_Workout_IdAndExercise_IdAndId(
                        workoutId, exercise.getId(), saved.getId()).orElseThrow();
        Instant originalCreatedAt = updated.getCreatedAt();
        updated.setWeight(50);
        updated.setReps(10);
        updated.setRpe(8);
        exerciseSetRepository.save(updated);
        entityManager.flush();
        entityManager.clear();

        ExerciseSet reloaded = exerciseSetRepository.findById(saved.getId()).orElseThrow();
        assertEquals(50, reloaded.getWeight());
        assertEquals(10, reloaded.getReps());
        assertEquals(8, reloaded.getRpe());
        assertEquals(originalCreatedAt, reloaded.getCreatedAt());
        assertEquals(exercise.getId(), reloaded.getExercise().getId());
        assertEquals(1, exerciseSetRepository.count());
    }

    @Test
    public void exerciseSetRepository_list_ScopesResultsToWorkoutAndExercise() {
        Exercise exercise = saveExercise();
        Exercise otherExercise = saveExercise();
        UUID workoutId = exercise.getWorkout().getId();
        assertTrue(exerciseSetRepository
                .findByExercise_Workout_IdAndExercise_IdOrderByCreatedAtAscIdAsc(workoutId, exercise.getId())
                .isEmpty());
        ExerciseSet saved = exerciseSetRepository.saveAndFlush(new ExerciseSet(40, 12, 6, exercise));
        exerciseSetRepository.saveAndFlush(new ExerciseSet(70, 6, 9, otherExercise));
        entityManager.clear();

        List<ExerciseSet> sets = exerciseSetRepository
                .findByExercise_Workout_IdAndExercise_IdOrderByCreatedAtAscIdAsc(workoutId, exercise.getId());
        assertEquals(List.of(saved.getId()), sets.stream().map(ExerciseSet::getId).toList());
        assertTrue(exerciseSetRepository
                .findByExercise_Workout_IdAndExercise_IdOrderByCreatedAtAscIdAsc(UUID.randomUUID(), exercise.getId())
                .isEmpty());
    }

    private void setCreationTime(ExerciseSet set, Instant createdAt) {
        entityManager.createQuery("update ExerciseSet s set s.createdAt = :createdAt where s.id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("id", set.getId())
                .executeUpdate();
    }
}
