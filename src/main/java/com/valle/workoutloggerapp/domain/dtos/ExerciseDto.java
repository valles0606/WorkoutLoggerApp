package com.valle.workoutloggerapp.domain.dtos;

import com.valle.workoutloggerapp.domain.entity.ExerciseName;

import java.util.UUID;

public record ExerciseDto(
        UUID id,
        ExerciseName exerciseName
) {
}
