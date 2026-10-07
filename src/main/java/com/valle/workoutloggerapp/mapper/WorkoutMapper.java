package com.valle.workoutloggerapp.mapper;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.WorkoutDto;
import com.valle.workoutloggerapp.domain.entity.Workout;
import jakarta.validation.Valid;

public interface WorkoutMapper {
    public CreateWorkoutRequest fromDto(CreateWorkoutRequestDto workoutDto);
    public WorkoutDto toDto(Workout workout);
    UpdateWorkoutRequest fromDto(UpdateWorkoutRequestDto updateWorkoutRequestDto);
}
