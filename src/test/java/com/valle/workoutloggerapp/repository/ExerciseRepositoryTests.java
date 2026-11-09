package com.valle.workoutloggerapp.repository;

import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.domain.entity.Workout;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.valle.workoutloggerapp.domain.entity.ExerciseName.LAT_PULLDOWN;
import static com.valle.workoutloggerapp.domain.entity.ExerciseName.PULL_UP;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ExerciseRepositoryTests {

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    WorkoutRepository workoutRepository;

    // tests here were written for added methods to our repository.

    /**
     * Checks that saving an exercise generates an ID.
     * Test documentation written using AI.
     */
    @Test
    public void exersideRepository_saveExercise_ReturnsSavedExercise() {
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

        Exercise savedExercise = exerciseRepository.save(exercise);

        assertNotNull(savedExercise.getId());
    }

    /**
     * Checks that the workout and exercise IDs retrieve the expected exercise.
     * Test assertions and documentation updated using AI.
     */
    @Test
    public void setExerciseRepository_findByWorkoutIdAndExerciseId_ReturnsExercise() {
        Workout workout = Workout.builder()
                .title("test")
                .workoutDate(LocalDate.of(2026, 10, 6))
                .workoutSummary("test passes if saved")
                .build();

        Workout savedWorkout = workoutRepository.save(workout);

        UUID workoutId = savedWorkout.getId();

        Exercise exercise = Exercise.builder()
                .exerciseName(LAT_PULLDOWN)
                .workout(workout)
                .build();

        Exercise savedExercise = exerciseRepository.save(exercise);

        UUID exerciseId = savedExercise.getId();

        Optional<Exercise> foundExercise = exerciseRepository.findByWorkoutIdAndId(workoutId, exerciseId);

        assertTrue(foundExercise.isPresent());
        assertEquals(exerciseId, foundExercise.orElseThrow().getId());
        assertEquals(workoutId, foundExercise.orElseThrow().getWorkout().getId());
    }


    /**
     * Checks that deleting an exercise by its workout and exercise IDs removes it.
     * Test documentation written using AI.
     */
    @Test
    public void exerciseRepository_deleteByWorkoutIdAndExerciseId_ReturnsEmptyOptional() {
        Workout workout = Workout.builder()
                .title("test")
                .workoutDate(LocalDate.of(2026, 10, 6))
                .workoutSummary("test passes if saved")
                .build();

        Workout savedWorkout = workoutRepository.save(workout);
        UUID workoutId = savedWorkout.getId();

        Exercise exercise = Exercise.builder()
                .exerciseName(LAT_PULLDOWN)
                .workout(workout)
                .build();

        Exercise savedExercise = exerciseRepository.save(exercise);
        UUID exerciseId = savedExercise.getId();

        exerciseRepository.deleteByWorkoutIdAndId(workoutId, exerciseId);

        Optional<Exercise> foundExercise = exerciseRepository.findByWorkoutIdAndId(workoutId, exerciseId);

        assertTrue(foundExercise.isEmpty());
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
     * Checks that a saved exercise reloads with its name, workout association, generated ID, and timestamp.
     * Test written using AI.
     */
    @Test
    public void exerciseRepository_save_PersistsExerciseAndWorkout() {
        Exercise saved = saveExercise();
        UUID workoutId = saved.getWorkout().getId();
        entityManager.clear();

        Exercise reloaded = exerciseRepository.findById(saved.getId()).orElseThrow();

        assertNotNull(reloaded.getId());
        assertEquals(LAT_PULLDOWN, reloaded.getExerciseName());
        assertEquals(workoutId, reloaded.getWorkout().getId());
        assertNotNull(reloaded.getCreatedAt());
    }

    /**
     * Checks that changing an exercise name persists without changing its workout or creating another exercise.
     * Test written using AI.
     */
    @Test
    public void exerciseRepository_saveExistingExercise_PersistsUpdates() {
        Exercise saved = saveExercise();
        UUID workoutId = saved.getWorkout().getId();
        entityManager.clear();

        Exercise updated = exerciseRepository
                .findByWorkoutIdAndId(workoutId, saved.getId()).orElseThrow();
        updated.setExerciseName(PULL_UP);
        exerciseRepository.save(updated);
        entityManager.flush();
        entityManager.clear();

        Exercise reloaded = exerciseRepository.findById(saved.getId()).orElseThrow();
        assertEquals(PULL_UP, reloaded.getExerciseName());
        assertEquals(workoutId, reloaded.getWorkout().getId());
        assertEquals(1, exerciseRepository.count());
    }

    /**
     * Checks that the locking lookup returns the exercise belonging to the requested workout.
     * Test written using AI.
     */
    @Test
    public void exerciseRepository_findWithLock_ReturnsMatchingExercise() {
        Exercise saved = saveExercise();
        UUID workoutId = saved.getWorkout().getId();
        entityManager.clear();

        Exercise found = exerciseRepository
                .findWithLockByWorkoutIdAndId(workoutId, saved.getId()).orElseThrow();

        assertEquals(saved.getId(), found.getId());
        assertEquals(workoutId, found.getWorkout().getId());
    }

    /**
     * Checks that a wrong workout ID cannot retrieve or delete an existing exercise.
     * Test written using AI.
     */
    @Test
    public void exerciseRepository_scopedMethods_RejectWrongWorkout() {
        Exercise saved = saveExercise();
        UUID wrongWorkoutId = UUID.randomUUID();

        assertTrue(exerciseRepository
                .findByWorkoutIdAndId(wrongWorkoutId, saved.getId()).isEmpty());
        assertTrue(exerciseRepository
                .findWithLockByWorkoutIdAndId(wrongWorkoutId, saved.getId()).isEmpty());

        exerciseRepository.deleteByWorkoutIdAndId(wrongWorkoutId, saved.getId());
        entityManager.flush();
        entityManager.clear();

        assertTrue(exerciseRepository.findById(saved.getId()).isPresent());
    }

}
