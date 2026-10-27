package com.valle.workoutloggerapp.mapper;

import com.valle.workoutloggerapp.domain.CreateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseSetRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseSetDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateExerciseSetRequestDto;
import com.valle.workoutloggerapp.domain.entity.ExerciseSet;

public interface ExerciseSetMapper {
    ExerciseSetDto toDto(ExerciseSet exerciseSet);
    CreateExerciseSetRequest fromDto(CreateExerciseSetRequestDto createExerciseSetRequestDto);
    UpdateExerciseSetRequest fromDto(UpdateExerciseSetRequestDto updateExerciseSetRequestDto);
}
