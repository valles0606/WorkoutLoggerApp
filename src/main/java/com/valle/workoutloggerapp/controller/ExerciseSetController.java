package com.valle.workoutloggerapp.controller;

import com.valle.workoutloggerapp.domain.CreateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseRequest;
import com.valle.workoutloggerapp.domain.UpdateExerciseSetRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseSetRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseSetDto;
import com.valle.workoutloggerapp.domain.dtos.UpdateExerciseSetRequestDto;
import com.valle.workoutloggerapp.domain.entity.ExerciseSet;
import com.valle.workoutloggerapp.mapper.ExerciseSetMapper;
import com.valle.workoutloggerapp.service.ExerciseService;
import com.valle.workoutloggerapp.service.ExerciseSetService;
import com.valle.workoutloggerapp.service.impl.ExerciseSetServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/workouts/{workoutId}/exercises/{exerciseId}/sets")
public class ExerciseSetController {

    private final ExerciseSetService exerciseSetService;
    private final ExerciseSetMapper exerciseSetMapper;

    public ExerciseSetController(ExerciseSetService exerciseSetService, ExerciseSetMapper exerciseSetMapper) {
        this.exerciseSetService = exerciseSetService;
        this.exerciseSetMapper = exerciseSetMapper;
    }

    @PostMapping
    public ResponseEntity<ExerciseSetDto> createExerciseSet(
            @PathVariable UUID workoutId, @PathVariable UUID exerciseId,
            @Valid @RequestBody CreateExerciseSetRequestDto createExerciseSetRequestDto) {
        CreateExerciseSetRequest createExerciseSetRequest = exerciseSetMapper.fromDto(createExerciseSetRequestDto);
        ExerciseSet exerciseSet = exerciseSetService.createExerciseSet(
                workoutId, exerciseId, createExerciseSetRequest);
        ExerciseSetDto exerciseSetDto = exerciseSetMapper.toDto(exerciseSet);
        return ResponseEntity.ok(exerciseSetDto);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseSetDto>> getAllExerciseSets(@PathVariable UUID workoutId, @PathVariable UUID exerciseId) {
        List<ExerciseSet> exerciseSets = exerciseSetService.listExerciseSets(workoutId, exerciseId);
        List<ExerciseSetDto> exerciseSetDtos = exerciseSets.stream().map(exerciseSetMapper::toDto).toList();
        return ResponseEntity.ok(exerciseSetDtos);
    }

    @GetMapping(path = "/{exerciseSetId}")
    public ResponseEntity<ExerciseSetDto> getExerciseSetById(@PathVariable UUID workoutId, @PathVariable UUID exerciseId, @PathVariable UUID exerciseSetId) {
        ExerciseSet exerciseSet = exerciseSetService.getExerciseSetById(workoutId, exerciseId,exerciseSetId);
        ExerciseSetDto exerciseSetDto = exerciseSetMapper.toDto(exerciseSet);
        return ResponseEntity.ok(exerciseSetDto);
    }

    @PutMapping("/{exerciseSetId}")
    public ResponseEntity<ExerciseSetDto> updateExerciseSetById(
            @PathVariable UUID workoutId, @PathVariable UUID exerciseId, @PathVariable UUID exerciseSetId,
            @Valid @RequestBody UpdateExerciseSetRequestDto updateExerciseSetRequestDto) {
        UpdateExerciseSetRequest updateExerciseSetRequest = exerciseSetMapper.fromDto(updateExerciseSetRequestDto);
        ExerciseSet exerciseSet = exerciseSetService.updateExerciseSet(workoutId, exerciseId, exerciseSetId, updateExerciseSetRequest);
        ExerciseSetDto exerciseSetDto = exerciseSetMapper.toDto(exerciseSet);
        return ResponseEntity.ok(exerciseSetDto);
    }

    @DeleteMapping(path = "/{exerciseSetId}")
    public ResponseEntity<Void> deleteExerciseSetById(@PathVariable UUID workoutId, @PathVariable UUID exerciseId, @PathVariable UUID exerciseSetId) {
        exerciseSetService.deleteExerciseSetById(workoutId, exerciseId, exerciseSetId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
