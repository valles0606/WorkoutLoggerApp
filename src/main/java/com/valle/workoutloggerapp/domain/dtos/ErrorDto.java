package com.valle.workoutloggerapp.domain.dtos;

public record ErrorDto(
        Integer statusCode,
        String error
) {
}
