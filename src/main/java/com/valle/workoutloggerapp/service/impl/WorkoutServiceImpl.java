package com.valle.workoutloggerapp.service.impl;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.exception.WorkoutNotFoundException;
import com.valle.workoutloggerapp.repository.WorkoutRepository;
import com.valle.workoutloggerapp.service.WorkoutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository workoutRepository;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @Override
    public Workout createWorkout(CreateWorkoutRequest createWorkoutRequest) {
        Workout workout = new Workout();
        workout.setTitle(createWorkoutRequest.title());
        workout.setWorkoutDate(createWorkoutRequest.workoutDate());
        workout.setWorkoutSummary(createWorkoutRequest.workoutSummary());

        return workoutRepository.save(workout);
    }

    @Override
    public void deleteWorkout(UUID workoutId) {
        workoutRepository.deleteById(workoutId);
    }

    @Override
    public List<Workout> listWorkouts(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("workoutDate").descending().and(Sort.by("id").descending()));
        Page<Workout> workouts = workoutRepository.findAll(pageable);
        return workouts.getContent();
    }

    @Override
    public Workout updateWorkout(UUID workoutId, UpdateWorkoutRequest updateWorkoutRequest) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> new WorkoutNotFoundException(workoutId));
        workout.setTitle(updateWorkoutRequest.title());
        workout.setWorkoutDate(updateWorkoutRequest.workoutDate());
        workout.setWorkoutSummary(updateWorkoutRequest.workoutSummary());

        return workoutRepository.save(workout);
    }

    @Override
    public Workout getWorkoutById(UUID workoutId) {
        return workoutRepository.findById(workoutId).orElseThrow(() -> new WorkoutNotFoundException(workoutId));
    }
}
