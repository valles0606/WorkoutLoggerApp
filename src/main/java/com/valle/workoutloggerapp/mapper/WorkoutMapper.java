package com.valle.workoutloggerapp.mapper;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.WorkoutDto;
import com.valle.workoutloggerapp.domain.entity.Workout;

public interface WorkoutMapper {
    CreateWorkoutRequest fromDto(CreateWorkoutRequestDto workoutDto);
}
