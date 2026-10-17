package com.valle.workoutloggerapp.domain.dtos;

import com.valle.workoutloggerapp.domain.entity.ExerciseName;
import jakarta.validation.constraints.NotNull;

public record UpdateExerciseRequestDto(
        @NotNull(message = ERROR_MESSAGE_EXERCISE_NAME_NULL)
        ExerciseName exerciseName
) {
    private static final String ERROR_MESSAGE_EXERCISE_NAME_NULL = "Exercise name cannot be null";
}
