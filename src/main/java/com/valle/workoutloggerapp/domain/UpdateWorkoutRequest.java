package com.valle.workoutloggerapp.domain;

import java.time.LocalDate;

public record UpdateWorkoutRequest(
        String title,
        LocalDate workoutDate,
        String workoutSummary
) {
}
