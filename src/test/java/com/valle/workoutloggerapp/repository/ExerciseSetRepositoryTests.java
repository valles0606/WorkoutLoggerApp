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

    /**
     * Checks that the next set number starts at 1, is not reserved by a read, and increases after saving.
     * Test documentation written using AI.
     */
    @Test
    public void exerciseSetRepository_findTopByExercideID_returnsTopExerciseSet() {
        Workout workout = Workout.builder()
                .title("test")
                .workoutDate(LocalDate.of(2026, 10, 6))
                .workoutSummary("test passes if saved")
                .build();

        workoutRepository.save(workout);

        Exercise exercise = Exercise.builder()
                .exerciseName(LAT_PULLDOWN)
                .workout(workout)
                .build();

        exerciseRepository.save(exercise);

        int setNum = exerciseSetRepository
                .findTopByExercise_IdOrderBySetNumberDesc(exercise.getId())
                .map(existingSet -> existingSet.getSetNumber() + 1)
                .orElse(1);

        int setNum2 = exerciseSetRepository
                .findTopByExercise_IdOrderBySetNumberDesc(exercise.getId())
                .map(existingSet -> existingSet.getSetNumber() + 1)
                .orElse(1);

        assertEquals(1, setNum);
        assertEquals(1, setNum2, "Reading the next number does not reserve it");

        ExerciseSet exerciseSet = ExerciseSet.builder()
                .setNumber(setNum)
                .weight(67)
                .reps(6)
                .rpe(7)
                .exercise(exercise)
                .build();

        setNum = exerciseSetRepository.saveAndFlush(exerciseSet).getSetNumber();
        assertEquals(1, setNum);

        setNum = exerciseSetRepository
                .findTopByExercise_IdOrderBySetNumberDesc(exercise.getId())
                .map(existingSet -> existingSet.getSetNumber() + 1)
                .orElse(1);

        assertEquals(2, setNum);
    }

    /**
     * Checks that concurrent set creation waits for the exercise lock and saves distinct set numbers.
     * Test written using AI; documentation updated using AI.
     */
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createExerciseSet_concurrentTransactions_waitForLockAndUseDifferentSetNumbers() throws Exception {
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
                return set.getSetNumber();
            }));
            awaitLatch(firstSetCreated);

            var second = workers.submit(() -> transaction.execute(status -> {
                secondTransactionStarted.countDown();
                ExerciseSet set = exerciseSetService.createExerciseSet(workoutId, exerciseId, request);
                // Signal before commit so a duplicate-number insert cannot hide a missing lock.
                secondSetCreated.countDown();
                return set.getSetNumber();
            }));
            awaitLatch(secondTransactionStarted);

            assertFalse(secondSetCreated.await(500, TimeUnit.MILLISECONDS),
                    "The second request must wait while the first transaction holds the exercise lock");

            allowFirstCommit.countDown();
            assertEquals(1, first.get(10, TimeUnit.SECONDS).intValue());
            assertEquals(2, second.get(10, TimeUnit.SECONDS).intValue());

            List<Integer> savedNumbers = transaction.execute(status -> exerciseSetRepository
                    .findByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc(workoutId, exerciseId)
                    .stream()
                    .map(ExerciseSet::getSetNumber)
                    .toList());
            assertEquals(List.of(1, 2), savedNumbers);
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
     * Checks that the requested exercise sets are returned in ascending set-number order.
     * Test assertions and documentation updated using AI.
     */
    @Test
    public void exerciseSetRepositoryfindByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc_returnsList_() {
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
                .setNumber(1)
                .weight(40)
                .reps(12)
                .rpe(6)
                .exercise(exercise)
                .build();

        ExerciseSet set2 = ExerciseSet.builder()
                .setNumber(2)
                .weight(50)
                .reps(10)
                .rpe(7)
                .exercise(exercise)
                .build();

        ExerciseSet set3 = ExerciseSet.builder()
                .setNumber(3)
                .weight(60)
                .reps(8)
                .rpe(8)
                .exercise(exercise)
                .build();

        ExerciseSet set4 = ExerciseSet.builder()
                .setNumber(4)
                .weight(65)
                .reps(6)
                .rpe(9)
                .exercise(exercise)
                .build();

        exerciseSetRepository.saveAllAndFlush(List.of(set1, set2, set3, set4));

        List<ExerciseSet> exerciseSets = exerciseSetRepository
                .findByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc(workout.getId(), exercise.getId());

        assertEquals(List.of(1, 2, 3, 4),
                exerciseSets.stream().map(ExerciseSet::getSetNumber).toList());
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
                .setNumber(1)
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
        assertEquals(1, found.getSetNumber());
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
                .setNumber(1)
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
                new ExerciseSet(1, 40, 12, 6, exercise));
        entityManager.clear();

        ExerciseSet reloaded = exerciseSetRepository.findById(saved.getId()).orElseThrow();

        assertNotNull(reloaded.getId());
        assertEquals(1, reloaded.getSetNumber());
        assertEquals(40, reloaded.getWeight());
        assertEquals(12, reloaded.getReps());
        assertEquals(6, reloaded.getRpe());
        assertEquals(exercise.getId(), reloaded.getExercise().getId());
    }

    /**
     * Checks that updated weight, reps, and RPE persist while the set number and exercise remain unchanged.
     * Test written using AI.
     */
    @Test
    public void exerciseSetRepository_saveExistingSet_PersistsUpdates() {
        Exercise exercise = saveExercise();
        UUID workoutId = exercise.getWorkout().getId();
        ExerciseSet saved = exerciseSetRepository.saveAndFlush(
                new ExerciseSet(1, 40, 12, 6, exercise));
        entityManager.clear();

        ExerciseSet updated = exerciseSetRepository
                .findByExercise_Workout_IdAndExercise_IdAndId(
                        workoutId, exercise.getId(), saved.getId()).orElseThrow();
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
        assertEquals(1, reloaded.getSetNumber());
        assertEquals(exercise.getId(), reloaded.getExercise().getId());
        assertEquals(1, exerciseSetRepository.count());
    }

    /**
     * Checks that an empty exercise has no top set and that the highest set number is scoped to its exercise.
     * Test written using AI.
     */
    @Test
    public void exerciseSetRepository_findTop_ReturnsHighestNumberForRequestedExercise() {
        Exercise exercise = saveExercise();
        Exercise otherExercise = saveExercise();
        assertTrue(exerciseSetRepository
                .findTopByExercise_IdOrderBySetNumberDesc(exercise.getId()).isEmpty());
        exerciseSetRepository.saveAllAndFlush(List.of(
                new ExerciseSet(3, 60, 8, 8, exercise),
                new ExerciseSet(1, 40, 12, 6, exercise),
                new ExerciseSet(2, 50, 10, 7, exercise),
                new ExerciseSet(9, 70, 6, 9, otherExercise)));
        entityManager.clear();

        ExerciseSet top = exerciseSetRepository
                .findTopByExercise_IdOrderBySetNumberDesc(exercise.getId()).orElseThrow();

        assertEquals(3, top.getSetNumber());
        assertEquals(exercise.getId(), top.getExercise().getId());
    }
}
