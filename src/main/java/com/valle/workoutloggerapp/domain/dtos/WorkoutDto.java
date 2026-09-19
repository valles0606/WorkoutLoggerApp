package com.valle.workoutloggerapp.domain.dtos;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public record WorkoutDto(
        UUID id,
        String title,
        LocalDate workoutDate,
        Timestamp startTime,
        Timestamp endTime,
        String workoutSummary
) {
}
