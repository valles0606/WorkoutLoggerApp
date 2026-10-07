package com.valle.workoutloggerapp.repository;

import com.valle.workoutloggerapp.domain.entity.Workout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDate;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class WorkoutRepositoryTest {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Test
    public void WorkoutRepository_SaveAll_ReturnSavedWorkout() {
        //Arrange
        Workout workout = Workout.builder().workoutDate(LocalDate.now())
                .workoutSummary("TestWorkout")
                .build();

        //Act
        Workout savedWorkout = workoutRepository.save(workout);

        //Assert
        Assertions.assertNotNull(savedWorkout);
        Assertions.assertNotNull(savedWorkout.getId());
    }
}
