package com.valle.workoutloggerapp.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "exercise_sets")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseSet {

    public ExerciseSet(Integer weight, Integer reps, Integer rpe, Exercise exercise) {
        this.weight = weight;
        this.reps = reps;
        this.rpe = rpe;
        this.exercise = exercise;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Min(0)
    private Integer weight;

    @Column(nullable = false)
    @Min(0)
    private Integer reps;

    @Column(nullable = false)
    @Min(1)
    @Max(10)
    private Integer rpe;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
}
