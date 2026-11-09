package com.valle.workoutloggerapp.repository;

import com.valle.workoutloggerapp.domain.entity.Workout;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class WorkoutRepositoryTests {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private EntityManager entityManager;

    /**
     * Checks that saving and flushing a workout generates an ID.
     * Test documentation written using AI.
     */
    @Test
    public void workoutRepository_saveWorkout_ReturnsSavedWorkout() {
        Workout workout = Workout.builder()
                .title("test")
                .workoutDate(LocalDate.of(2026, 10, 6))
                .workoutSummary("test passes if saved")
                .build();

        Workout savedWorkout = workoutRepository.saveAndFlush(workout);

        assertNotNull(savedWorkout.getId());
    }

    /**
     * Checks that finding a saved workout reloads its ID, title, date, and summary.
     * Test documentation written using AI.
     */
    @Test
    public void workoutRepository_findWorkoutById_ReturnsWorkout() {
        Workout workout = Workout.builder()
                .title("test")
                .workoutDate(LocalDate.of(2026, 10, 6))
                .workoutSummary("test passes if saved")
                .build();

        Workout savedWorkout = workoutRepository.save(workout);
        UUID id = savedWorkout.getId();

        entityManager.flush();
        entityManager.clear();

        Workout foundWorkout = workoutRepository.findById(id).orElseThrow();

        assertEquals(id, foundWorkout.getId());
        assertEquals("test", foundWorkout.getTitle());
        assertEquals(LocalDate.of(2026, 10, 6), foundWorkout.getWorkoutDate());
        assertEquals("test passes if saved", foundWorkout.getWorkoutSummary());
    }

    /**
     * Checks that an unknown workout ID returns an empty Optional.
     * Test documentation written using AI.
     */
    @Test
    public void workoutRepository_findWorkoutByUnknownId_ReturnsEmpty() {
        Optional<Workout> foundWorkout = workoutRepository.findById(UUID.randomUUID());

        assertTrue(foundWorkout.isEmpty());
    }



    /**
     * Checks that saving an existing workout persists changed fields without creating another workout.
     * Test written using AI.
     */
    @Test
    public void workoutRepository_saveExistingWorkout_PersistsUpdates() {
        Workout workout = workoutRepository.save(Workout.builder()
                .title("Original").workoutDate(LocalDate.of(2026, 10, 6)).build());
        UUID id = workout.getId();
        entityManager.flush();
        entityManager.clear();

        Workout updated = workoutRepository.findById(id).orElseThrow();
        updated.setTitle("Updated");
        updated.setWorkoutDate(LocalDate.of(2026, 10, 7));
        updated.setWorkoutSummary("Updated summary");
        workoutRepository.save(updated);
        entityManager.flush();
        entityManager.clear();

        Workout reloaded = workoutRepository.findById(id).orElseThrow();
        assertEquals("Updated", reloaded.getTitle());
        assertEquals(LocalDate.of(2026, 10, 7), reloaded.getWorkoutDate());
        assertEquals("Updated summary", reloaded.getWorkoutSummary());
        assertEquals(id, reloaded.getId());
        assertEquals(1, workoutRepository.count());
    }

    /**
     * Checks that workouts are returned from newest to oldest workout date.
     * Test written using AI.
     */
    @Test
    public void workoutRepository_findAll_ReturnsWorkoutsByDateDescending() {
        Workout older = workoutRepository.save(Workout.builder()
                .title("Older").workoutDate(LocalDate.of(2026, 10, 5)).build());
        Workout newer = workoutRepository.save(Workout.builder()
                .title("Newer").workoutDate(LocalDate.of(2026, 10, 7)).build());
        Workout middle = workoutRepository.save(Workout.builder()
                .title("Middle").workoutDate(LocalDate.of(2026, 10, 6)).build());
        entityManager.flush();
        entityManager.clear();

        var workouts = workoutRepository.findAll(Sort.by(Sort.Direction.DESC, "workoutDate"));

        assertEquals(List.of(newer.getId(), middle.getId(), older.getId()),
                workouts.stream().map(Workout::getId).toList());
    }

    /**
     * Checks that deleting one workout removes it while preserving another workout.
     * Test written using AI.
     */
    @Test
    public void workoutRepository_deleteById_RemovesOnlyRequestedWorkout() {
        Workout deleted = workoutRepository.save(Workout.builder()
                .title("Delete").workoutDate(LocalDate.of(2026, 10, 6)).build());
        Workout retained = workoutRepository.save(Workout.builder()
                .title("Keep").workoutDate(LocalDate.of(2026, 10, 7)).build());

        workoutRepository.deleteById(deleted.getId());
        entityManager.flush();
        entityManager.clear();

        assertTrue(workoutRepository.findById(deleted.getId()).isEmpty());
        assertTrue(workoutRepository.findById(retained.getId()).isPresent());
    }

}
