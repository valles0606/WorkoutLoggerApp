package com.valle.workoutloggerapp.mapper.impl;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseDto;
import com.valle.workoutloggerapp.domain.dtos.FullExerciseDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.mapper.ExerciseMapper;
import com.valle.workoutloggerapp.mapper.ExerciseSetMapper;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapperImpl implements ExerciseMapper {

    private final ExerciseSetMapper exerciseSetMapper;

    public ExerciseMapperImpl(ExerciseSetMapper exerciseSetMapper) {
        this.exerciseSetMapper = exerciseSetMapper;
    }

    @Override
    public CreateExerciseRequest fromDto(CreateExerciseRequestDto createExerciseRequestDto) {
        return new CreateExerciseRequest(
                createExerciseRequestDto.exerciseName()
        );
    }

    @Override
    public ExerciseDto toDto(Exercise exercise) {
        return new ExerciseDto(
                exercise.getId(),
                exercise.getExerciseName()
        );
    }

    @Override
    public UpdateExerciseRequest fromDto(UpdateExerciseRequestDto updateExerciseRequestDto) {
        return new UpdateExerciseRequest(
                updateExerciseRequestDto.exerciseName()
        );
    }

    @Override
    public FullExerciseDto toFullDto(Exercise exercise) {
        //TODO: Create ExerciseSets Mapper interface and implementation! Needed to finish exercise controller class.
        return new FullExerciseDto(
                exercise.getId(),
                exercise.getExerciseName(),
                exercise.getExerciseSets().stream().map(exerciseSetMapper::toDto).toList()
        );
    }
}