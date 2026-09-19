package com.valle.workoutloggerapp.domain;

import java.sql.Timestamp;
import java.time.LocalDate;

public record CreateWorkoutRequest(String title, LocalDate workoutDate, Timestamp startTime, Timestamp endTime,
                                   String workoutSummary) {
}
