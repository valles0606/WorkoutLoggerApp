package com.valle.workoutloggerapp.mapper.impl;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.mapper.ExerciseMapper;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapperImpl implements ExerciseMapper {

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
}
