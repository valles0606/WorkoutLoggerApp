package com.valle.workoutloggerapp.controller;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseDto;
import com.valle.workoutloggerapp.domain.dtos.FullExerciseDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.entity.Exercise;
import com.valle.workoutloggerapp.mapper.ExerciseMapper;
import com.valle.workoutloggerapp.service.ExerciseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    public ResponseEntity<List<ExerciseDto>> getAllExercises(
            @PathVariable UUID workoutId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size
    ) {
        List<Exercise> exercises = exerciseService.listExercises(workoutId, page, size);
        List<ExerciseDto> exerciseDtos = exercises.stream().map(exerciseMapper::toDto).toList();
        return ResponseEntity.ok(exerciseDtos);
    }

    @GetMapping(path = "/{exerciseId}")
    public ResponseEntity<FullExerciseDto> getExerciseById(@PathVariable UUID workoutId, @PathVariable UUID exerciseId) {
        Exercise exercise = exerciseService.getExerciseById(workoutId, exerciseId);
        FullExerciseDto exerciseDto = exerciseMapper.toFullDto(exercise);
        return ResponseEntity.ok(exerciseDto);
    }

    @PutMapping(path = "/{exerciseId}")
    public ResponseEntity<ExerciseDto> updateExercise(
            @PathVariable UUID workoutId, @PathVariable UUID exerciseId,
            @Valid @RequestBody UpdateExerciseRequestDto updateExerciseRequestDto) {
        UpdateExerciseRequest updateExerciseRequest = exerciseMapper.fromDto(updateExerciseRequestDto);
        Exercise exercise = exerciseService.updateExercise(workoutId, exerciseId, updateExerciseRequest);
        ExerciseDto exerciseDto = exerciseMapper.toDto(exercise);
        return ResponseEntity.ok(exerciseDto);
    }

    @DeleteMapping(path = "/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(@PathVariable UUID workoutId, @PathVariable UUID exerciseId) {
        exerciseService.deleteExercise(workoutId, exerciseId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
