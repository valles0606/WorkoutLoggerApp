package com.valle.workoutloggerapp.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "exercises")
@Getter
@Setter
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseName exerciseName;

    //cascadeType.all makes it so deleting an exercise will also delete all sets with it!
    //Orphan removal makes it so removing a set from our kist wil also delete it from our database, not just unlink it
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseSet> exerciseSets;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;
}
