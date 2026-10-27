package com.valle.workoutloggerapp.mapper.impl;

import com.valle.workoutloggerapp.domain.CreateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseSetRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseSetDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateExerciseSetRequestDto;
import com.valle.workoutloggerapp.domain.entity.ExerciseSet;
import com.valle.workoutloggerapp.mapper.ExerciseSetMapper;
import org.springframework.stereotype.Component;

@Component
public class ExerciseSetMapperImpl implements ExerciseSetMapper {

    @Override
    public ExerciseSetDto toDto(ExerciseSet exerciseSet) {
        return new ExerciseSetDto(
                exerciseSet.getId(),
                exerciseSet.getSetNumber(),
                exerciseSet.getWeight(),
                exerciseSet.getReps(),
                exerciseSet.getRpe()
        );
    }

    @Override
    public CreateExerciseSetRequest fromDto(CreateExerciseSetRequestDto createExerciseSetRequestDto) {
        return new CreateExerciseSetRequest(
                createExerciseSetRequestDto.weight(),
                createExerciseSetRequestDto.reps(),
                createExerciseSetRequestDto.rpe());
    }

    @Override
    public UpdateExerciseSetRequest fromDto(UpdateExerciseSetRequestDto updateExerciseSetRequestDto) {
        return new UpdateExerciseSetRequest(
                updateExerciseSetRequestDto.weight(),
                updateExerciseSetRequestDto.reps(),
                updateExerciseSetRequestDto.rpe()
        );
    }

}
