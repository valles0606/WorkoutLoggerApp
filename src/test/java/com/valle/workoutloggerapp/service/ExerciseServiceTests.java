package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.domain.entity.ExerciseName;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.exception.ExerciseNotFoundException;
import com.valle.workoutloggerapp.exception.WorkoutNotFoundException;
import com.valle.workoutloggerapp.repository.ExerciseRepository;
import com.valle.workoutloggerapp.repository.WorkoutRepository;
import com.valle.workoutloggerapp.service.impl.ExerciseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
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
    public void ExerciseService_createExercise_returnsExercise() {
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

    @Test
    public void ExerciseService_listExercises_returnsListOfExercises() {
        UUID workoutId = UUID.randomUUID();
        Workout workout = Workout.builder().id(workoutId).build();

        Exercise exercise1 = Exercise.builder()
                .id(UUID.randomUUID())
                .exerciseName(ExerciseName.BARBELL_BENCH_PRESS)
                .workout(workout)
                .build();

        Exercise exercise2 = Exercise.builder()
                .id(UUID.randomUUID())
                .exerciseName(ExerciseName.ARNOLD_PRESS)
                .workout(workout)
                .build();

        Exercise exercise3 = Exercise.builder()
                .id(UUID.randomUUID())
                .exerciseName(ExerciseName.BARBELL_DEADLIFT)
                .workout(workout)
                .build();

        List<Exercise> exercises = List.of(exercise1, exercise2, exercise3);
        PageRequest pageable = PageRequest.of(1, 3,
                Sort.by("createdAt").descending().and(Sort.by("id").descending()));

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));
        when(exerciseRepository.findByWorkoutIdOrderByCreatedAtDesc(workoutId, pageable))
                .thenReturn(new PageImpl<>(exercises, pageable, 6));

        List<Exercise> result = exerciseService.listExercises(workoutId, 1, 3);

        assertEquals(exercises, result);
        verify(workoutRepository).findById(workoutId);
        verify(exerciseRepository).findByWorkoutIdOrderByCreatedAtDesc(workoutId, pageable);
    }

    @Test
    public void ExerciseService_createExercise_throwsWorkoutNotFoundException() {
        UUID workoutId = UUID.randomUUID();
        CreateExerciseRequest request = new CreateExerciseRequest(ExerciseName.BARBELL_BENCH_PRESS);
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        WorkoutNotFoundException exception = assertThrows(WorkoutNotFoundException.class,
                () -> exerciseService.createExercise(workoutId, request));

        assertEquals(workoutId, exception.getWorkoutId());
        verifyNoInteractions(exerciseRepository);
    }

    @Test
    public void ExerciseService_listExercises_returnsEmptyList() {
        UUID workoutId = UUID.randomUUID();
        Workout workout = Workout.builder().id(workoutId).build();
        PageRequest pageable = PageRequest.of(0, 10,
                Sort.by("createdAt").descending().and(Sort.by("id").descending()));
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workout));
        when(exerciseRepository.findByWorkoutIdOrderByCreatedAtDesc(workoutId, pageable))
                .thenReturn(Page.empty(pageable));

        List<Exercise> result = exerciseService.listExercises(workoutId, 0, 10);

        assertTrue(result.isEmpty());
        verify(workoutRepository).findById(workoutId);
        verify(exerciseRepository).findByWorkoutIdOrderByCreatedAtDesc(workoutId, pageable);
    }

    @Test
    public void ExerciseService_listExercises_throwsWorkoutNotFoundException() {
        UUID workoutId = UUID.randomUUID();
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        WorkoutNotFoundException exception = assertThrows(WorkoutNotFoundException.class,
                () -> exerciseService.listExercises(workoutId, 0, 10));

        assertEquals(workoutId, exception.getWorkoutId());
        verifyNoInteractions(exerciseRepository);
    }

    @Test
    public void ExerciseService_getExerciseById_returnsExercise() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        Workout workout = Workout.builder().id(workoutId).build();
        Exercise exercise = Exercise.builder()
                .id(exerciseId)
                .exerciseName(ExerciseName.ARNOLD_PRESS)
                .workout(workout)
                .build();
        when(exerciseRepository.findByWorkoutIdAndId(workoutId, exerciseId))
                .thenReturn(Optional.of(exercise));

        Exercise result = exerciseService.getExerciseById(workoutId, exerciseId);

        assertSame(exercise, result);
        verify(exerciseRepository).findByWorkoutIdAndId(workoutId, exerciseId);
    }

    @Test
    public void ExerciseService_getExerciseById_throwsExerciseNotFoundException() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        when(exerciseRepository.findByWorkoutIdAndId(workoutId, exerciseId))
                .thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.getExerciseById(workoutId, exerciseId));

        assertEquals(exerciseId, exception.getExerciseId());
        verify(exerciseRepository).findByWorkoutIdAndId(workoutId, exerciseId);
    }

    @Test
    public void ExerciseService_updateExercise_returnsUpdatedExercise() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        Workout workout = Workout.builder().id(workoutId).build();
        Exercise existingExercise = Exercise.builder()
                .id(exerciseId)
                .exerciseName(ExerciseName.ARNOLD_PRESS)
                .workout(workout)
                .build();
        UpdateExerciseRequest request = new UpdateExerciseRequest(ExerciseName.BARBELL_BENCH_PRESS);
        Exercise repositoryResult = Exercise.builder()
                .id(exerciseId)
                .exerciseName(request.exerciseName())
                .workout(workout)
                .build();
        when(exerciseRepository.findByWorkoutIdAndId(workoutId, exerciseId))
                .thenReturn(Optional.of(existingExercise));
        when(exerciseRepository.save(existingExercise)).thenReturn(repositoryResult);

        Exercise result = exerciseService.updateExercise(workoutId, exerciseId, request);

        ArgumentCaptor<Exercise> captor = ArgumentCaptor.forClass(Exercise.class);
        verify(exerciseRepository).save(captor.capture());
        assertSame(existingExercise, captor.getValue());
        assertEquals(request.exerciseName(), captor.getValue().getExerciseName());
        assertEquals(exerciseId, captor.getValue().getId());
        assertSame(workout, captor.getValue().getWorkout());
        assertSame(repositoryResult, result);
    }

    @Test
    public void ExerciseService_updateExercise_throwsExerciseNotFoundException() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        UpdateExerciseRequest request = new UpdateExerciseRequest(ExerciseName.BARBELL_BENCH_PRESS);
        when(exerciseRepository.findByWorkoutIdAndId(workoutId, exerciseId))
                .thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseService.updateExercise(workoutId, exerciseId, request));

        assertEquals(exerciseId, exception.getExerciseId());
        verify(exerciseRepository, never()).save(any(Exercise.class));
    }

    @Test
    public void ExerciseService_deleteExercise_returnsVoid() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();

        exerciseService.deleteExercise(workoutId, exerciseId);

        verify(exerciseRepository).deleteByWorkoutIdAndId(workoutId, exerciseId);
    }
}
