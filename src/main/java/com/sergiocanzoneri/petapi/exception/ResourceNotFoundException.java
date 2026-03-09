package com.sergiocanzoneri.petapi.exception;

// Exception thrown when a resource is not found.
public class ResourceNotFoundException extends RuntimeException {

    // Default constructor with message
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
