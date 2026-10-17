package com.valle.workoutloggerapp.mapper.impl;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.FullWorkoutDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.WorkoutDto;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.mapper.ExerciseMapper;
import com.valle.workoutloggerapp.mapper.WorkoutMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkoutMapperImpl implements WorkoutMapper {

    private final ExerciseMapper exerciseMapper;

    public WorkoutMapperImpl(ExerciseMapper exerciseMapper) {
        this.exerciseMapper = exerciseMapper;
    }

    @Override
    public CreateWorkoutRequest fromDto(CreateWorkoutRequestDto workoutDto) {
        return new CreateWorkoutRequest(
                workoutDto.title(),
                workoutDto.workoutDate(),
                workoutDto.workoutSummary()
        );
    }

    @Override
    public WorkoutDto toDto(Workout workout) {
        return new  WorkoutDto(
                workout.getId(),
                workout.getTitle(),
                workout.getWorkoutDate(),
                workout.getWorkoutSummary()
        );
    }

    @Override
    public UpdateWorkoutRequest fromDto(UpdateWorkoutRequestDto updateWorkoutRequestDto) {
        return new UpdateWorkoutRequest(
                updateWorkoutRequestDto.title(),
                updateWorkoutRequestDto.workoutDate(),
                updateWorkoutRequestDto.workoutSummary()
        );
    }

    @Override
    public FullWorkoutDto toFullDto(Workout workout) {
        return new FullWorkoutDto(
                workout.getId(),
                workout.getTitle(),
                workout.getWorkoutDate(),
                workout.getWorkoutSummary(),
                workout.getExercises().stream()
                        .map(exerciseMapper::toDto)
                        .toList()
        );
    }
}
