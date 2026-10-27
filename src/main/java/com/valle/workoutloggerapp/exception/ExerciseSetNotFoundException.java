package com.valle.workoutloggerapp.exception;

import java.util.UUID;

public class ExerciseSetNotFoundException extends RuntimeException {

    private final UUID exerciseSetId;

    public ExerciseSetNotFoundException(UUID exerciseId, UUID exerciseSetId) {
        this.exerciseSetId = exerciseSetId;

        super("Exercise Set with id " + exerciseSetId + " not found in Exercise with id  " + exerciseId);
    }

    public UUID getExerciseSetId() {
        return exerciseSetId;
    }
}
