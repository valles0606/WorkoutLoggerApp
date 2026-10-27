package com.valle.workoutloggerapp.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "exercise_sets",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_exercise_sets_exercise_set_number",
                columnNames = {"exercise_id", "set_number"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class ExerciseSet {

    public ExerciseSet(Integer setNumber, Integer weight, Integer reps, Integer rpe, Exercise exercise) {
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.rpe = rpe;
        this.exercise = exercise;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "set_number", nullable = false)
    @Min(1)
    private Integer setNumber;

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
