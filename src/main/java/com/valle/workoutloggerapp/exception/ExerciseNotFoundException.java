package com.valle.workoutloggerapp.exception;

import java.util.UUID;

public class ExerciseNotFoundException extends RuntimeException {

    private final UUID exerciseId;
    public ExerciseNotFoundException(UUID workoutId, UUID exerciseId) {
        this.exerciseId = exerciseId;
        super("Exercise with ID " + exerciseId + " not found in Workout with ID " + workoutId);
    }

    public UUID getExerciseId() {
        return exerciseId;
    }
}
