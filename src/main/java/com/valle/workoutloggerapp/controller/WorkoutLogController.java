package com.valle.workoutloggerapp.controller;


import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.dtos.CreateWorkoutRequestDto;
import com.valle.workoutloggerapp.domain.dtos.WorkoutDto;
import com.valle.workoutloggerapp.mapper.WorkoutMapper;
import com.valle.workoutloggerapp.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//controller has been set up with versioning
@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutLogController {

    private final WorkoutService workoutService;
    private final WorkoutMapper workoutMapper;

    public WorkoutLogController(WorkoutService workoutService, WorkoutMapper workoutMapper) {
        this.workoutService = workoutService;
        this.workoutMapper = workoutMapper;
    }

    /*TODO: design service controller to receive get, post, and  delete mapping requests,
       sending appropriate response codes as well.
       IMPORTANT: Make sure to validate data before it hits our repository!
       E.G. end time CANNOT be less than start time.
    */

    @PostMapping
    public ResponseEntity<WorkoutDto> createWorkout(
            @Valid @RequestBody CreateWorkoutRequestDto createWorkoutRequestDto) {
        CreateWorkoutRequest workoutRequest = workoutMapper.fromDto(createWorkoutRequestDto);

    }

}
