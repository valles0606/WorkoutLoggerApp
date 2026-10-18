package com.valle.workoutloggerapp.mapper;

import com.valle.workoutloggerapp.domain.dtos.ExerciseSetDto;
import com.valle.workoutloggerapp.domain.entity.ExerciseSet;

public interface ExerciseSetMapper {
    public ExerciseSetDto toDto(ExerciseSet exerciseSet);
}
