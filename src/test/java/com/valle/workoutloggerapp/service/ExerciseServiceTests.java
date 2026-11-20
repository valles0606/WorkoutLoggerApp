package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.domain.entity.ExerciseName;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.repository.ExerciseRepository;
import com.valle.workoutloggerapp.repository.WorkoutRepository;
import com.valle.workoutloggerapp.service.impl.ExerciseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExerciseServiceTests {

    @Mock
    private ExerciseRepository exerciseRepository;
    @InjectMocks
    private ExerciseServiceImpl exerciseService;
    @Mock
    private WorkoutRepository workoutRepository;

    @Test
    public void exerciseService_createExercise_returnsExercise() {
        CreateExerciseRequest createExerciseRequest = new CreateExerciseRequest (
                ExerciseName.BARBELL_BENCH_PRESS
        );

        UUID workoutId = UUID.randomUUID();
        Workout workout = Workout.builder().id(workoutId).build();

        Exercise repositoryResult = Exercise.builder()
                .id(UUID.randomUUID())
                .exerciseName(ExerciseName.BARBELL_BENCH_PRESS)
                .workout(workout)
                .build();

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(repositoryResult);

        Exercise result = exerciseService.createExercise(workoutId, createExerciseRequest);

        ArgumentCaptor<Exercise> captor = ArgumentCaptor.forClass(Exercise.class);
        verify(workoutRepository).findById(workoutId);
        verify(exerciseRepository).save(captor.capture());

        assertEquals(createExerciseRequest.exerciseName(), captor.getValue().getExerciseName());
        assertSame(workout, captor.getValue().getWorkout());
        assertSame(repositoryResult, result);
    }

}
