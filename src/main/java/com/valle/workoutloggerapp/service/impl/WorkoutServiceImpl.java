package com.valle.workoutloggerapp.service.impl;

import com.valle.workoutloggerapp.domain.CreateWorkoutRequest;
import com.valle.workoutloggerapp.domain.UpdateWorkoutRequest;
import com.valle.workoutloggerapp.domain.entity.Workout;
import com.valle.workoutloggerapp.exception.WorkoutNotFoundException;
import com.valle.workoutloggerapp.repository.WorkoutRepository;
import com.valle.workoutloggerapp.service.WorkoutService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutRepository workoutRepository;

    public WorkoutServiceImpl(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @Override
    public Workout createWorkout(CreateWorkoutRequest createWorkoutRequest) {
        Workout workout = new Workout();
        workout.setTitle(createWorkoutRequest.title());
        workout.setWorkoutDate(createWorkoutRequest.workoutDate());
        workout.setWorkoutSummary(createWorkoutRequest.workoutSummary());

        return workoutRepository.save(workout);
    }

    @Override
    public void deleteWorkout(UUID workoutId) {
        workoutRepository.deleteById(workoutId);
    }

    @Override
    public List<Workout> listWorkouts() {
        return workoutRepository.findAll(Sort.by(Sort.Direction.DESC, "workoutDate"));
    }

    @Override
    public Workout updateWorkout(UUID workoutId, UpdateWorkoutRequest updateWorkoutRequest) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(() -> new WorkoutNotFoundException(workoutId));
        workout.setTitle(updateWorkoutRequest.title());
        workout.setWorkoutDate(updateWorkoutRequest.workoutDate());
        workout.setWorkoutSummary(updateWorkoutRequest.workoutSummary());

        return workoutRepository.save(workout);
    }
}
