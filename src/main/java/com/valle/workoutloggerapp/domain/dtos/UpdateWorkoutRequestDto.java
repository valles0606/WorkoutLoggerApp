package com.valle.workoutloggerapp.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record UpdateWorkoutRequestDto(
        @NotBlank(message = ERROR_MESSAGE_TITLE_NULL)
        @Length(max = 100, message = ERROR_MESSAGE_TITLE_LENGTH)
        String title,
        @NotNull(message = ERROR_MESSAGE_WORKOUT_DATE_NULL)
        LocalDate workoutDate,
        @Length(max = 1000, message = ERROR_MESSAGE_WORKOUT_SUMMARY_LENGTH)
        String workoutSummary
) {
    public static final String ERROR_MESSAGE_TITLE_LENGTH = "Title must be between 1 to 100 characters.";
    public static final String ERROR_MESSAGE_WORKOUT_SUMMARY_LENGTH = "Workout summary exceeds 1000 characters.";
    public static final String ERROR_MESSAGE_WORKOUT_DATE_NULL = "Workout date can not be null.";
    public static final String ERROR_MESSAGE_TITLE_NULL = "Title can not be null.";
}
