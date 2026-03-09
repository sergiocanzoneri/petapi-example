package com.sergiocanzoneri.petapi.util;

/**
 * Centralized constants for API paths, response keys, and shared messages
 * used across the application and tests.
 */
public final class PetApiConstants {

    private PetApiConstants() {
    }

    // API paths

    // Base path for the pets REST API
    public static final String PETS_BASE_PATH = "/api/pets";

    // Path template for a single pet by id: use with path variable "{id}"
    public static final String PETS_ID_PATH = PETS_BASE_PATH + "/{id}";

    

    // Test / URL

    // URL prefix for local server in tests
    public static final String LOCALHOST_URL_PREFIX = "http://localhost:";



    // Error response body keys

    // JSON key for error message in error responses
    public static final String ERROR_KEY = "error";

    // JSON key for validation/details in error responses
    public static final String DETAILS_KEY = "details";


    // Messages (API / exceptions)

    // Message returned when request validation fails
    public static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

    // Prefix for pet not found exception message
    public static final String PET_NOT_FOUND_PREFIX = "Pet not found with id: ";

    // Message returned when age filter combines exact age with min/max range
    public static final String INVALID_FILTER_AGE_MESSAGE =
            "Cannot combine exact age filter with min/max age range; use either age or minAge/maxAge.";

    // Message returned when deserializing pet response fails in tests
    public static final String DESERIALIZE_PET_ERROR_MESSAGE = "Failed to deserialize pet response";

    // Default message for invalid field in validation details
    public static final String INVALID_FIELD_DEFAULT = "invalid";
}
