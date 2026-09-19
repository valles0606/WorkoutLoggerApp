package com.valle.workoutloggerapp.domain.dtos;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import java.sql.Timestamp;
import java.time.LocalDate;

public record CreateWorkoutRequestDto(
        @NotBlank @Length(max = 100, message = ERROR_MESSAGE_TITLE_LENGTH) String title,
        LocalDate workoutDate,

        //TODO: add a custom class to validate that the end time is after the start time.
        Timestamp startTime,
        Timestamp endTime,
        @Length(max = 1000, message = ERROR_MESSAGE_WORKOUT_SUMMARY_LENGTH)
        String workoutSummary
) {
    public static final String ERROR_MESSAGE_TITLE_LENGTH = "Title must be between 1 to 100 characters.";
    public static final String ERROR_MESSAGE_WORKOUT_SUMMARY_LENGTH = "Workout summary must be between 1 to 1000 characters.";
}
