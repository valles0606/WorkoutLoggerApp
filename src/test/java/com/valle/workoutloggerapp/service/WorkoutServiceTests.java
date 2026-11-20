package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.repository.WorkoutRepository;
import com.valle.workoutloggerapp.service.impl.WorkoutServiceImpl;
import org.h2.mvstore.Page;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceTests {

    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    @Test
    public void workoutService_createWorkout_returnsWorkout() {
        CreateWorkoutRequest createWorkoutRequest = new CreateWorkoutRequest(
                "test1",
                LocalDate.of(2026, 11, 9),
                "test1 description"
        );

        Workout repositoryResult = Workout.builder()
                .id(UUID.randomUUID())
                .title(createWorkoutRequest.title())
                .workoutDate(createWorkoutRequest.workoutDate())
                .workoutSummary(createWorkoutRequest.workoutSummary())
                .build();
        when(workoutRepository.save(any(Workout.class))).thenReturn(repositoryResult);

        Workout savedWorkout = workoutService.createWorkout(createWorkoutRequest);

        ArgumentCaptor<Workout> workoutCaptor = ArgumentCaptor.forClass(Workout.class);
        verify(workoutRepository).save(workoutCaptor.capture());
        Workout submittedWorkout = workoutCaptor.getValue();

        assertEquals(createWorkoutRequest.title(), submittedWorkout.getTitle());
        assertEquals(createWorkoutRequest.workoutDate(), submittedWorkout.getWorkoutDate());
        assertEquals(createWorkoutRequest.workoutSummary(), submittedWorkout.getWorkoutSummary());
        assertSame(repositoryResult, savedWorkout);
    }

    @Test
    public void workoutService_listWorkouts_returnsListOfWorkouts() {
        Workout newer = Workout.builder()
                .title("test")
                .workoutDate(LocalDate.of(2026, 11, 9))
                .build();
        Workout older = Workout.builder()
                .title("test2")
                .workoutDate(LocalDate.of(2026, 11, 8))
                .build();

        List<Workout> workouts = List.of(newer, older);
        Sort expectedSort = Sort.by(Sort.Direction.DESC, "workoutDate");

        when(workoutRepository.findAll(expectedSort))
                .thenReturn(workouts);

        List<Workout> savedWorkouts = workoutService.listWorkouts();

        assertEquals(workouts, savedWorkouts);

        verify(workoutRepository).findAll(expectedSort);
    }

    @Test
    public void workoutService_getWorkoutById_returnsWorkout() {
        CreateWorkoutRequest createWorkoutRequest = new CreateWorkoutRequest(
                "test1",
                LocalDate.of(2026, 11, 9),
                "test1 description"
        );

        UUID workoutId = UUID.randomUUID();

        Workout repositoryResult = Workout.builder()
                .id(workoutId)
                .title(createWorkoutRequest.title())
                .workoutDate(createWorkoutRequest.workoutDate())
                .workoutSummary(createWorkoutRequest.workoutSummary())
                .build();

        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(repositoryResult));

        Workout savedWorkout = workoutService.getWorkoutById(workoutId);


        verify(workoutRepository).findById(workoutId);
        assertSame(repositoryResult, savedWorkout);
        assertEquals(workoutId, savedWorkout.getId());
        assertEquals(LocalDate.of(2026, 11, 9), savedWorkout.getWorkoutDate());
        assertEquals("test1", savedWorkout.getTitle());
        assertEquals("test1 description", savedWorkout.getWorkoutSummary());
    }

    @Test
    public void workoutService_updateWorkoutById_returnsWorkout() {
        UUID workoutId = UUID.randomUUID();

        Workout existingWorkout = Workout.builder()
                .id(workoutId)
                .title("Original title")
                .workoutDate(LocalDate.of(2026, 11, 9))
                .workoutSummary("Original summary")
                .build();

        UpdateWorkoutRequest request = new UpdateWorkoutRequest(
                "Updated title",
                LocalDate.of(2026, 11, 10),
                "Updated summary"
        );

        when(workoutRepository.findById(workoutId))
                .thenReturn(Optional.of(existingWorkout));
        when(workoutRepository.save(existingWorkout))
                .thenReturn(existingWorkout);

        Workout updatedWorkout = workoutService.updateWorkout(workoutId, request);

        verify(workoutRepository).findById(workoutId);
        verify(workoutRepository).save(existingWorkout);

        assertSame(existingWorkout, updatedWorkout);
        assertEquals(workoutId, updatedWorkout.getId());
        assertEquals(request.title(), updatedWorkout.getTitle());
        assertEquals(request.workoutDate(), updatedWorkout.getWorkoutDate());
        assertEquals(request.workoutSummary(), updatedWorkout.getWorkoutSummary());
    }

    @Test
    public void workoutService_deleteWorkoutById_returnsEmpty() {
        UUID id  = UUID.randomUUID();

        workoutService.deleteWorkout(id);

        verify(workoutRepository).deleteById(id);
    }

}
