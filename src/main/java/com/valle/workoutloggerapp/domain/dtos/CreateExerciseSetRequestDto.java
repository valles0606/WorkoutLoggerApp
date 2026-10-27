package com.valle.workoutloggerapp.domain.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateExerciseSetRequestDto(
        @NotNull(message = ERROR_MESSAGE_WEIGHT_NULL)
        @Min(value = 0, message = ERROR_MESSAGE_WEIGHT_BELOW_MIN)
        Integer weight,

        @NotNull(message = ERROR_MESSAGE_REPS_NULL)
        @Min(value = 0, message =  ERROR_MESSAGE_REPS_BELOW_MIN)
        Integer reps,

        @NotNull(message = ERROR_MESSAGE_RPE_NULL)
        @Min(value = 1, message =  ERROR_MESSAGE_RPE_BELOW_MIN)
        @Max(value = 10, message = ERROR_MESSAGE_RPE_ABOVE_MAX)
        Integer rpe
) {
    private static final String ERROR_MESSAGE_WEIGHT_NULL = "Weight can not be null";
    private static final String ERROR_MESSAGE_REPS_NULL = "Reps can not be null";
    private static final String ERROR_MESSAGE_RPE_NULL = "Rpe can not be null";

    private static final String ERROR_MESSAGE_WEIGHT_BELOW_MIN = "Weight is invalid, cannot be less than 0";
    private static final String ERROR_MESSAGE_REPS_BELOW_MIN = "Reps is invalid, cannot be less than 0";
    private static final String ERROR_MESSAGE_RPE_BELOW_MIN = "Rpe is invalid, cannot be less than 1";
    private static final String ERROR_MESSAGE_RPE_ABOVE_MAX = "Rpe is invalid, cannot be more than 10";
}
