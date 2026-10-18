package com.valle.workoutloggerapp.domain.dtos;

import com.valle.workoutloggerapp.domain.entity.ExerciseName;

import java.util.List;
import java.util.UUID;

public record FullExerciseDto(
        UUID id,
        ExerciseName exerciseName,
        List<ExerciseSetDto> exerciseSets
) {
}
