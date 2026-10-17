package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;

import java.util.UUID;

public interface ExerciseService {

    public Exercise createExercise(UUID workoutId, CreateExerciseRequest createExerciseRequest);
}
