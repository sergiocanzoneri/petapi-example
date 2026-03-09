package com.sergiocanzoneri.petapi.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// DTO to be used as body for create requests.
public record PetInDto (

        // Name is required
        @NotBlank(message = "name is required")
        String name,

        // Species is required
        @NotBlank(message = "species is required")
        String species,

        // Age is required and must be greater than or equal to 0
        @Min(value = 0, message = "age must be greater than or equal to 0")
        Integer age,

        // Owner name is not required
        String ownerName
) {
}
