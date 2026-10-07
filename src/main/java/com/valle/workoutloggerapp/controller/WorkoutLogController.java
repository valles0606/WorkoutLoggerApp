package com.valle.workoutloggerapp.controller;


import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.WorkoutDto;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.mapper.WorkoutMapper;
import com.valle.workoutloggerapp.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutLogController {

    private final WorkoutService workoutService;
    private final WorkoutMapper workoutMapper;

    public WorkoutLogController(WorkoutService workoutService, WorkoutMapper workoutMapper) {
        this.workoutService = workoutService;
        this.workoutMapper = workoutMapper;
    }

    @PostMapping
    public ResponseEntity<WorkoutDto> createWorkout(@Valid @RequestBody CreateWorkoutRequestDto createWorkoutRequestDto) {
        CreateWorkoutRequest workoutRequest = workoutMapper.fromDto(createWorkoutRequestDto);
        Workout workout = workoutService.createWorkout(workoutRequest);
        WorkoutDto createdWorkout = workoutMapper.toDto(workout);
        return new ResponseEntity<>(createdWorkout, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutDto>> listWorkouts() {
        List<Workout> workouts = workoutService.listWorkouts();
        List<WorkoutDto> workoutDtos = workouts.stream().map(workoutMapper::toDto).toList();
        return ResponseEntity.ok(workoutDtos);
    }

    @PutMapping(path = "/{workoutId}")
    public ResponseEntity<WorkoutDto> updateWorkout(@PathVariable UUID workoutId, @Valid @RequestBody UpdateWorkoutRequestDto updateWorkoutRequestDto) {
        UpdateWorkoutRequest updateWorkoutRequest = workoutMapper.fromDto(updateWorkoutRequestDto);
        Workout workout = workoutService.updateWorkout(workoutId, updateWorkoutRequest);
        WorkoutDto updatedWorkout = workoutMapper.toDto(workout);
        return ResponseEntity.ok(updatedWorkout);
    }

    @DeleteMapping(path = "/{workoutId}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable UUID workoutId) {
        workoutService.deleteWorkout(workoutId);
        return new  ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
