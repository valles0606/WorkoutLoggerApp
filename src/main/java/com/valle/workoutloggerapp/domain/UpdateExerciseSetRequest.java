package com.valle.workoutloggerapp.domain;

public record UpdateExerciseSetRequest(
        Integer weight,
        Integer reps,
        Integer rpe
) {
}
