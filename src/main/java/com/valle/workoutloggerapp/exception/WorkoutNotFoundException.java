package com.valle.workoutloggerapp.exception;

import java.util.UUID;

public class WorkoutNotFoundException extends RuntimeException {
    private final UUID workoutId;

    public WorkoutNotFoundException(UUID workoutId) {
        super("Workout with id " + workoutId + " not found");
        this.workoutId = workoutId;
    }

    public UUID getWorkoutId() {
        return workoutId;
    }
}
