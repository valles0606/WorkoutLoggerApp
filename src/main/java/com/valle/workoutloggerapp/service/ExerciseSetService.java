package com.valle.workoutloggerapp.service;

import com.valle.workoutloggerapp.domain.CreateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.entity.ExerciseSet;

import java.util.List;
import java.util.UUID;

public interface ExerciseSetService {
    ExerciseSet createExerciseSet(UUID workoutId, UUID exerciseId, CreateExerciseSetRequest createExerciseSetRequest);

    List<ExerciseSet> listExerciseSets(UUID workoutId, UUID exerciseId);

    ExerciseSet getExerciseSetById(UUID workoutId, UUID exerciseId, UUID exerciseSetId);

    void deleteExerciseSetById(UUID workoutId, UUID exerciseId, UUID exerciseSetId);

    ExerciseSet updateExerciseSet(UUID workoutId, UUID exerciseId, UUID exerciseSetId, UpdateExerciseSetRequest updateExerciseSetRequest);
}
