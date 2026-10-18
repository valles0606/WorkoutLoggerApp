package com.valle.workoutloggerapp.mapper.impl;

import com.valle.workoutloggerapp.domain.dtos.ExerciseSetDto;
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
}
