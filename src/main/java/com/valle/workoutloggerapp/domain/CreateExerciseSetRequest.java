package com.valle.workoutloggerapp.domain;

public record CreateExerciseSetRequest(
        Integer weight,
        Integer reps,
        Integer rpe
) {
}
