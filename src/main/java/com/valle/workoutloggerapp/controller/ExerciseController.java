package com.valle.workoutloggerapp.controller;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseDto;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.mapper.ExerciseMapper;
import com.valle.workoutloggerapp.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workouts/{workoutId}/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final ExerciseMapper exerciseMapper;

    public ExerciseController(ExerciseService exerciseService, ExerciseMapper exerciseMapper) {
        this.exerciseService = exerciseService;
        this.exerciseMapper = exerciseMapper;
    }

    @PostMapping
    public ResponseEntity<ExerciseDto> createExercise(
            @PathVariable UUID workoutId,
            @Valid @RequestBody CreateExerciseRequestDto createExerciseRequestDto) {
        CreateExerciseRequest createExerciseRequest = exerciseMapper.fromDto(createExerciseRequestDto);
        Exercise exercise = exerciseService.createExercise(workoutId, createExerciseRequest);
        ExerciseDto exerciseDto = exerciseMapper.toDto(exercise);
        return new  ResponseEntity<>(exerciseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseDto>> getAllExercises(@PathVariable UUID workoutId) {
        List<Exercise> exercises = exerciseService.listExercises(workoutId);
        List<ExerciseDto> exerciseDtos = exercises.stream().map(exerciseMapper::toDto).toList();
        return ResponseEntity.ok(exerciseDtos);
    }

}
