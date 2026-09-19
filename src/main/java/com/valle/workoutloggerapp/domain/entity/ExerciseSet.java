package com.valle.workoutloggerapp.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "ExerciseSets")
@Getter
@Setter
public class ExerciseSet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @Column(nullable = false)
    private Boolean failure;

    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
}
