package com.valle.workoutloggerapp.mapper;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.FullWorkoutDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.WorkoutDto;
import com.valle.workoutloggerapp.domain.entity.Workout;
import jakarta.validation.Valid;

public interface WorkoutMapper {
    CreateWorkoutRequest fromDto(CreateWorkoutRequestDto workoutDto);
    WorkoutDto toDto(Workout workout);
    UpdateWorkoutRequest fromDto(UpdateWorkoutRequestDto updateWorkoutRequestDto);
    FullWorkoutDto toFullDto(Workout workout);
}
