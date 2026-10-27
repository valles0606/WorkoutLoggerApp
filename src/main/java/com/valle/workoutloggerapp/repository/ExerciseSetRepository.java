package com.valle.workoutloggerapp.repository;

import com.valle.workoutloggerapp.domain.entity.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, UUID> {
    Optional<ExerciseSet> findTopByExercise_IdOrderBySetNumberDesc(UUID exerciseId);
    List<ExerciseSet> findByExercise_Workout_IdAndExercise_IdOrderBySetNumberAsc(
            UUID workoutId, UUID exerciseId);
    Optional<ExerciseSet> findByExercise_Workout_IdAndExercise_IdAndId(UUID workoutId, UUID exerciseId, UUID exerciseSetId);
    void deleteByExercise_Workout_IdAndExercise_IdAndId(UUID workoutId, UUID exerciseId, UUID exerciseSetId);
}