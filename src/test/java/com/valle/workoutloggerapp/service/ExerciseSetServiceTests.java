package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.domain.entity.ExerciseSet;
import com.valle.workoutloggerapp.exception.ExerciseNotFoundException;
import com.valle.workoutloggerapp.exception.ExerciseSetNotFoundException;
import com.valle.workoutloggerapp.repository.ExerciseRepository;
import com.valle.workoutloggerapp.repository.ExerciseSetRepository;
import com.valle.workoutloggerapp.service.impl.ExerciseSetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExerciseSetServiceTests {

    @Mock
    private ExerciseSetRepository exerciseSetRepository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @InjectMocks
    private ExerciseSetServiceImpl exerciseSetService;

    @Test
    public void ExerciseSetService_createExerciseSet_returnsFirstSet() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        Exercise exercise = Exercise.builder().id(exerciseId).build();
        CreateExerciseSetRequest request = new CreateExerciseSetRequest(100, 8, 7);
        ExerciseSet repositoryResult = ExerciseSet.builder()
                .id(UUID.randomUUID()).setNumber(1)
                .weight(request.weight()).reps(request.reps()).rpe(request.rpe())
                .exercise(exercise).build();
        when(exerciseRepository.findWithLockByWorkoutIdAndId(workoutId, exerciseId))
                .thenReturn(Optional.of(exercise));
        when(exerciseSetRepository.findTopByExercise_IdOrderBySetNumberDesc(exerciseId))
                .thenReturn(Optional.empty());
        when(exerciseSetRepository.save(any(ExerciseSet.class))).thenReturn(repositoryResult);

        ExerciseSet result = exerciseSetService.createExerciseSet(workoutId, exerciseId, request);

        ArgumentCaptor<ExerciseSet> captor = ArgumentCaptor.forClass(ExerciseSet.class);
        verify(exerciseSetRepository).save(captor.capture());
        ExerciseSet submittedSet = captor.getValue();
        assertEquals(1, submittedSet.getSetNumber());
        assertEquals(request.weight(), submittedSet.getWeight());
        assertEquals(request.reps(), submittedSet.getReps());
        assertEquals(request.rpe(), submittedSet.getRpe());
        assertSame(exercise, submittedSet.getExercise());
        assertSame(repositoryResult, result);
    }

    @Test
    public void ExerciseSetService_createExerciseSet_returnsNextNumberedSet() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        Exercise exercise = Exercise.builder().id(exerciseId).build();
        CreateExerciseSetRequest request = new CreateExerciseSetRequest(120, 6, 8);
        ExerciseSet latestSet = ExerciseSet.builder().setNumber(4).exercise(exercise).build();
        ExerciseSet repositoryResult = ExerciseSet.builder()
                .id(UUID.randomUUID()).setNumber(5)
                .weight(request.weight()).reps(request.reps()).rpe(request.rpe())
                .exercise(exercise).build();
        when(exerciseRepository.findWithLockByWorkoutIdAndId(workoutId, exerciseId))
                .thenReturn(Optional.of(exercise));
        when(exerciseSetRepository.findTopByExercise_IdOrderBySetNumberDesc(exerciseId))
                .thenReturn(Optional.of(latestSet));
        when(exerciseSetRepository.save(any(ExerciseSet.class))).thenReturn(repositoryResult);

        ExerciseSet result = exerciseSetService.createExerciseSet(workoutId, exerciseId, request);

        ArgumentCaptor<ExerciseSet> captor = ArgumentCaptor.forClass(ExerciseSet.class);
        verify(exerciseSetRepository).save(captor.capture());
        assertEquals(5, captor.getValue().getSetNumber());
        assertEquals(request.weight(), captor.getValue().getWeight());
        assertEquals(request.reps(), captor.getValue().getReps());
        assertEquals(request.rpe(), captor.getValue().getRpe());
        assertSame(exercise, captor.getValue().getExercise());
        assertSame(repositoryResult, result);
    }

    @Test
    public void ExerciseSetService_createExerciseSet_throwsExerciseNotFoundException() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        CreateExerciseSetRequest request = new CreateExerciseSetRequest(100, 8, 7);
        when(exerciseRepository.findWithLockByWorkoutIdAndId(workoutId, exerciseId))
                .thenReturn(Optional.empty());

        ExerciseNotFoundException exception = assertThrows(ExerciseNotFoundException.class,
                () -> exerciseSetService.createExerciseSet(workoutId, exerciseId, request));

        assertEquals(exerciseId, exception.getExerciseId());
        verifyNoInteractions(exerciseSetRepository);
    }

    @Test
    public void ExerciseSetService_listExerciseSets_returnsListOfExerciseSets() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        Exercise exercise = Exercise.builder().id(exerciseId).build();
        ExerciseSet first = ExerciseSet.builder().id(UUID.randomUUID())
                .setNumber(1).exercise(exercise).build();
        ExerciseSet second = ExerciseSet.builder().id(UUID.randomUUID())
                .setNumber(2).exercise(exercise).build();
        List<ExerciseSet> sets = List.of(first, second);
        when(exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc(workoutId, exerciseId))
                .thenReturn(sets);

        List<ExerciseSet> result = exerciseSetService.listExerciseSets(workoutId, exerciseId);

        assertEquals(sets, result);
        verify(exerciseSetRepository)
                .findByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc(workoutId, exerciseId);
    }

    @Test
    public void ExerciseSetService_listExerciseSets_returnsEmptyList() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        when(exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc(workoutId, exerciseId))
                .thenReturn(List.of());

        List<ExerciseSet> result = exerciseSetService.listExerciseSets(workoutId, exerciseId);

        assertTrue(result.isEmpty());
        verify(exerciseSetRepository)
                .findByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc(workoutId, exerciseId);
    }

    @Test
    public void ExerciseSetService_getExerciseSetById_returnsExerciseSet() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();
        ExerciseSet set = ExerciseSet.builder().id(setId).setNumber(1).build();
        when(exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, setId))
                .thenReturn(Optional.of(set));

        ExerciseSet result = exerciseSetService.getExerciseSetById(workoutId, exerciseId, setId);

        assertSame(set, result);
        verify(exerciseSetRepository).findByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, setId);
    }

    @Test
    public void ExerciseSetService_getExerciseSetById_throwsExerciseSetNotFoundException() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();
        when(exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, setId))
                .thenReturn(Optional.empty());

        ExerciseSetNotFoundException exception = assertThrows(ExerciseSetNotFoundException.class,
                () -> exerciseSetService.getExerciseSetById(workoutId, exerciseId, setId));

        assertEquals(setId, exception.getExerciseSetId());
        verify(exerciseSetRepository).findByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, setId);
    }

    @Test
    public void ExerciseSetService_updateExerciseSet_returnsUpdatedExerciseSet() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();
        Exercise exercise = Exercise.builder().id(exerciseId).build();
        ExerciseSet existingSet = ExerciseSet.builder().id(setId).setNumber(3)
                .weight(100).reps(8).rpe(7).exercise(exercise).build();
        UpdateExerciseSetRequest request = new UpdateExerciseSetRequest(110, 6, 9);
        ExerciseSet repositoryResult = ExerciseSet.builder().id(setId).setNumber(3)
                .weight(request.weight()).reps(request.reps()).rpe(request.rpe())
                .exercise(exercise).build();
        when(exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, setId))
                .thenReturn(Optional.of(existingSet));
        when(exerciseSetRepository.save(existingSet)).thenReturn(repositoryResult);

        ExerciseSet result = exerciseSetService.updateExerciseSet(workoutId, exerciseId, setId, request);

        ArgumentCaptor<ExerciseSet> captor = ArgumentCaptor.forClass(ExerciseSet.class);
        verify(exerciseSetRepository).save(captor.capture());
        ExerciseSet submittedSet = captor.getValue();
        assertSame(existingSet, submittedSet);
        assertEquals(request.weight(), submittedSet.getWeight());
        assertEquals(request.reps(), submittedSet.getReps());
        assertEquals(request.rpe(), submittedSet.getRpe());
        assertEquals(setId, submittedSet.getId());
        assertEquals(3, submittedSet.getSetNumber());
        assertSame(exercise, submittedSet.getExercise());
        assertSame(repositoryResult, result);
    }

    @Test
    public void ExerciseSetService_updateExerciseSet_throwsExerciseSetNotFoundException() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();
        UpdateExerciseSetRequest request = new UpdateExerciseSetRequest(110, 6, 9);
        when(exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, setId))
                .thenReturn(Optional.empty());

        ExerciseSetNotFoundException exception = assertThrows(ExerciseSetNotFoundException.class,
                () -> exerciseSetService.updateExerciseSet(workoutId, exerciseId, setId, request));

        assertEquals(setId, exception.getExerciseSetId());
        verify(exerciseSetRepository, never()).save(any(ExerciseSet.class));
    }

    @Test
    public void ExerciseSetService_deleteExerciseSetById_returnsVoid() {
        UUID workoutId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();
        UUID setId = UUID.randomUUID();

        exerciseSetService.deleteExerciseSetById(workoutId, exerciseId, setId);

        verify(exerciseSetRepository).deleteByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, setId);
    }
}
