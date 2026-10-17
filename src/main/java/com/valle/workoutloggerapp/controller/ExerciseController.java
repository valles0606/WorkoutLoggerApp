package com.valle.workoutloggerapp.controller;

import com.valle.workoutloggerapp.domain.CreateExerciseRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateExerciseRequestDto;
import com.valle.workoutloggerapp.domain.dtos.ExerciseDto;
import com.valle.workoutloggerapp.mapper.ExerciseMapper;
import com.valle.workoutloggerapp.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    }

}
