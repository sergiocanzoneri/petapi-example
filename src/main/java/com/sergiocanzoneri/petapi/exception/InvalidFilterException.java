package com.sergiocanzoneri.petapi.exception;

// Thrown when filter parameters are conflicting.
public class InvalidFilterException extends RuntimeException {

    public InvalidFilterException(String message) {
        super(message);
    }
}
