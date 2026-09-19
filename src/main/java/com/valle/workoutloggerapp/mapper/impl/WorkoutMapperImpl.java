package com.valle.workoutloggerapp.mapper.impl;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateWorkoutRequestDto;
import com.valle.workoutloggerapp.mapper.WorkoutMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkoutMapperImpl implements WorkoutMapper {
    @Override
    public CreateWorkoutRequest fromDto(CreateWorkoutRequestDto workoutDto) {
        return new CreateWorkoutRequest(workoutDto.title(), workoutDto.workoutDate(), workoutDto.startTime(),
                workoutDto.endTime(), workoutDto.workoutSummary());
    }
}
