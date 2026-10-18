package com.valle.workoutloggerapp.repository;

import com.valle.workoutloggerapp.domain.entity.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, UUID> {
    Optional<ExerciseSet> findTopByExercise_IdOrderBySetNumberDesc(UUID exerciseId);
}
