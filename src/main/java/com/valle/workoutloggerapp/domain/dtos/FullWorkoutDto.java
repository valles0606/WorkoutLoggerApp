package com.valle.workoutloggerapp.domain.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FullWorkoutDto(
        UUID id,
        String title,
        LocalDate workoutDate,
        String workoutSummary,
        List<ExerciseDto> exercises
) {
}
