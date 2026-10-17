package com.valle.workoutloggerapp.domain;

import com.valle.workoutloggerapp.domain.entity.ExerciseName;

public record CreateExerciseRequest(
        ExerciseName exerciseName
) {
}
