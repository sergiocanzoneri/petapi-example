package com.sergiocanzoneri.petapi.controller.dto;

import java.util.Objects;

import jakarta.validation.constraints.Min;

// DTO for filtering pets in search operations.
public record PetFilterDto(
        String species,
        String ownerName,
        String name,
        @Min(value = 0, message = "age must be greater than or equal to 0") Integer age,
        @Min(value = 0, message = "minAge must be greater than or equal to 0") Integer minAge,
        @Min(value = 0, message = "maxAge must be greater than or equal to 0") Integer maxAge
) {

    public boolean hasAnyFilter() {
        return Objects.nonNull(species)
                || Objects.nonNull(ownerName)
                || Objects.nonNull(name)
                || Objects.nonNull(age)
                || Objects.nonNull(minAge)
                || Objects.nonNull(maxAge);
    }

    /**
     * NOTE: Age filtering can use either:
     * - exact age (age) or
     * - a range (minAge and/or maxAge)
     * but not both, to avoid conflicts.
     */


    // True if exact age filter is set
    public boolean hasExactAge() {
        return Objects.nonNull(age);
    }

    // True if any age range filter (minAge or maxAge) is set
    public boolean hasRangeAge() {
        return Objects.nonNull(minAge) || Objects.nonNull(maxAge);
    }

    // True when both exact age and age range are requested; such a request is invalid
    public boolean hasConflictingAgeFilters() {
        return hasExactAge() && hasRangeAge();
    }
}
