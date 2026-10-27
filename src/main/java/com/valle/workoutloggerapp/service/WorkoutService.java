package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.entity.Workout;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutService {
    Workout createWorkout(CreateWorkoutRequest createWorkoutRequest);
    void deleteWorkout(UUID workoutId);
    List<Workout> listWorkouts();
    Workout updateWorkout(UUID workoutId, UpdateWorkoutRequest updateWorkoutRequest);
    Workout getWorkoutById(UUID workoutId);
}
