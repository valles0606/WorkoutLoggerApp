package com.valle.workoutloggerapp.service.impl;

import com.valle.workoutloggerapp.domain.CreateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.domain.entity.ExerciseSet;
import com.valle.workoutloggerapp.exception.ExerciseNotFoundException;
import com.valle.workoutloggerapp.exception.ExerciseSetNotFoundException;
import com.valle.workoutloggerapp.repository.ExerciseRepository;
import com.valle.workoutloggerapp.repository.ExerciseSetRepository;
import com.valle.workoutloggerapp.service.ExerciseSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExerciseSetServiceImpl implements ExerciseSetService {

    private final ExerciseSetRepository exerciseSetRepository;
    private final ExerciseRepository exerciseRepository;

    public ExerciseSetServiceImpl(ExerciseSetRepository exerciseSetRepository, ExerciseRepository exerciseRepository) {
        this.exerciseSetRepository = exerciseSetRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    @Transactional
    public ExerciseSet createExerciseSet(UUID workoutId, UUID exerciseId, CreateExerciseSetRequest createExerciseSetRequest) {
        Exercise exercise = exerciseRepository.findWithLockByWorkoutIdAndId(workoutId, exerciseId)
                .orElseThrow(() -> new ExerciseNotFoundException(workoutId, exerciseId));

        int setNum = exerciseSetRepository
                .findTopByExercise_IdOrderBySetNumberDesc(exercise.getId())
                .map(existingSet -> existingSet.getSetNumber() + 1)
                .orElse(1);

        ExerciseSet exerciseSet = new ExerciseSet(
                setNum,
                createExerciseSetRequest.weight(),
                createExerciseSetRequest.reps(),
                createExerciseSetRequest.rpe(),
                exercise
        );

        return exerciseSetRepository.save(exerciseSet);
    }

    @Override
    public List<ExerciseSet> listExerciseSets(UUID workoutId, UUID exerciseId) {
        return exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc(workoutId, exerciseId);
    }

    @Override
    public ExerciseSet getExerciseSetById(UUID workoutId, UUID exerciseId, UUID exerciseSetId) {
        return exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, exerciseSetId)
                .orElseThrow(() -> new ExerciseSetNotFoundException(exerciseId, exerciseSetId));
    }

    @Transactional
    @Override
    public void deleteExerciseSetById(UUID workoutId, UUID exerciseId, UUID exerciseSetId) {
        exerciseSetRepository.deleteByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, exerciseSetId);
    }

    @Override
    public ExerciseSet updateExerciseSet(UUID workoutId, UUID exerciseId, UUID exerciseSetId,
                                         UpdateExerciseSetRequest updateExerciseSetRequest) {
        ExerciseSet exerciseSet = exerciseSetRepository.findByExercise_Workout_IdAndExercise_IdAndId(workoutId, exerciseId, exerciseSetId)
                .orElseThrow(() -> new ExerciseSetNotFoundException(workoutId, exerciseSetId));
        exerciseSet.setWeight(updateExerciseSetRequest.weight());
        exerciseSet.setReps(updateExerciseSetRequest.reps());
        exerciseSet.setRpe(updateExerciseSetRequest.rpe());
        return exerciseSetRepository.save(exerciseSet);
    }


}
