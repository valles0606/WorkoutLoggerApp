package com.valle.workoutloggerapp.service.impl;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.exception.ExerciseNotFoundException;
import com.valle.workoutloggerapp.exception.WorkoutNotFoundException;
import com.valle.workoutloggerapp.repository.ExerciseRepository;
import com.valle.workoutloggerapp.repository.WorkoutRepository;
import com.valle.workoutloggerapp.service.ExerciseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository, WorkoutRepository workoutRepository) {
        this.exerciseRepository = exerciseRepository;
        this.workoutRepository = workoutRepository;
    }

    @Override
    @Transactional
    public Exercise createExercise(UUID workoutId, CreateExerciseRequest createExerciseRequest) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(workoutId));

        Exercise exercise = new Exercise();
        exercise.setExerciseName(createExerciseRequest.exerciseName());
        exercise.setWorkout(workout);

        return exerciseRepository.save(exercise);
    }

    @Override
    public List<Exercise> listExercises(UUID workoutId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(workoutId));
        return workout.getExercises();
    }

    @Override
    public Exercise getExerciseById(UUID workoutId, UUID exerciseId) {
        return exerciseRepository.findByWorkoutIdAndId(workoutId, exerciseId)
                .orElseThrow(() -> new ExerciseNotFoundException(workoutId, exerciseId));
    }
}
