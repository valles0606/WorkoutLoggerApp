package com.valle.workoutloggerapp.domain;
import java.time.LocalDate;

public record CreateWorkoutRequest(
        String title,
        LocalDate workoutDate,
        String workoutSummary) {
}
