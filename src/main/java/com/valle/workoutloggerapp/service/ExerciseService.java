package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;

import java.util.List;
import java.util.UUID;

public interface ExerciseService {

    Exercise createExercise(UUID workoutId, CreateExerciseRequest createExerciseRequest);
    List<Exercise> listExercises(UUID workoutId);
    Exercise getExerciseById(UUID workoutId, UUID exerciseId);
    void deleteExercise(UUID workoutId, UUID exerciseId);
    Exercise updateExercise(UUID workoutId, UUID exerciseId, UpdateExerciseRequest updateExerciseRequest);
}
