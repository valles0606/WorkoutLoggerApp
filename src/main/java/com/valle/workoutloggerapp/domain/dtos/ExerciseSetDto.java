package com.valle.workoutloggerapp.domain.dtos;

import java.util.UUID;

public record ExerciseSetDto(
        UUID id,
        Integer weight,
        Integer reps,
        Integer rpe
) {
}
