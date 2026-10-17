package com.valle.workoutloggerapp.domain;

import com.valle.workoutloggerapp.domain.entity.ExerciseName;

public record UpdateExerciseRequest(
        ExerciseName exerciseName
) {
}
