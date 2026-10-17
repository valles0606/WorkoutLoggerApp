package com.valle.workoutloggerapp.mapper;


import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.entity.Exercise;

public interface ExerciseMapper {
    public CreateExerciseRequest fromDto(CreateExerciseRequestDto createExerciseRequestDto);
    public ExerciseDto toDto(Exercise exercise);
    public UpdateExerciseRequest fromDto(UpdateExerciseRequestDto updateExerciseRequestDto);
}
