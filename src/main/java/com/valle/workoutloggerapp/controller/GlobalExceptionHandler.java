package com.valle.workoutloggerapp.controller;

import com.valle.workoutloggerapp.domain.dtos.ErrorDto;
import com.valle.workoutloggerapp.exception.ExerciseNotFoundException;
import com.valle.workoutloggerapp.exception.ExerciseSetNotFoundException;
import com.valle.workoutloggerapp.exception.WorkoutNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            WorkoutNotFoundException.class,
            ExerciseNotFoundException.class,
            ExerciseSetNotFoundException.class
    })
    public ResponseEntity<ErrorDto> handleNotFoundException(
            RuntimeException e) {

        ErrorDto error = new ErrorDto(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest()
                .body(new ErrorDto(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleUnreadableRequestException(
            HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                "Request body must contain valid JSON with the expected field types and enum values."
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        String message = UUID.class.equals(e.getRequiredType())
                ? "Parameter '" + e.getName() + "' must be a valid UUID."
                : "Parameter '" + e.getName() + "' has an invalid value.";

        return ResponseEntity.badRequest()
                .body(new ErrorDto(HttpStatus.BAD_REQUEST.value(), message));
    }
}
