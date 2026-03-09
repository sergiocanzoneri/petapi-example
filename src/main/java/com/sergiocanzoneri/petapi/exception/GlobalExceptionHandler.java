package com.sergiocanzoneri.petapi.exception;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sergiocanzoneri.petapi.util.PetApiConstants;

// Global exception handler for the application.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle ResourceNotFoundException, return 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(PetApiConstants.ERROR_KEY, ex.getMessage()));
    }

    // Handle InvalidFilterException, return 400 Bad Request
    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFilter(InvalidFilterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(PetApiConstants.ERROR_KEY, ex.getMessage()));
    }

    // Handle MethodArgumentNotValidException, return 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> Objects.nonNull(e.getDefaultMessage()) ? e.getDefaultMessage() : PetApiConstants.INVALID_FIELD_DEFAULT,
                        (a, b) -> b
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(PetApiConstants.ERROR_KEY, PetApiConstants.VALIDATION_FAILED_MESSAGE, PetApiConstants.DETAILS_KEY, errors));
    }
}
