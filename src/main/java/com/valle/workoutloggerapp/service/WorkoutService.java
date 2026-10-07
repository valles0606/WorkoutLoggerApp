package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.entity.Workout;

import java.util.List;
import java.util.UUID;

public interface WorkoutService {
    public Workout createWorkout(CreateWorkoutRequest createWorkoutRequest);
    public void deleteWorkout(UUID workoutId);
    public List<Workout> listWorkouts();
    public Workout updateWorkout(UUID workoutId, UpdateWorkoutRequest updateWorkoutRequest);
}
