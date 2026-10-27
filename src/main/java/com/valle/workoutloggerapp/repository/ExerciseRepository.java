package com.valle.workoutloggerapp.repository;

import com.valle.workoutloggerapp.domain.entity.Exercise;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    Optional<Exercise> findByWorkoutIdAndId(UUID workoutId, UUID exerciseId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Exercise> findWithLockByWorkoutIdAndId(UUID workoutId, UUID exerciseId);

    void deleteByWorkoutIdAndId(UUID workoutId, UUID exerciseId);
}
