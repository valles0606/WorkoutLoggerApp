package com.valle.workoutloggerapp.service.impl;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.service.ExerciseService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    @Override
    public Exercise createExercise(UUID workoutId, CreateExerciseRequest createExerciseRequest) {
        return null;
    }
}
