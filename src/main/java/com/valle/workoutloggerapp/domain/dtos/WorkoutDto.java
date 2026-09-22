package com.valle.workoutloggerapp.domain.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record WorkoutDto(
        UUID id,
        String title,
        LocalDate workoutDate,
        String workoutSummary
) {
}
