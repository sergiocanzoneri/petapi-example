package com.sergiocanzoneri.petapi.controller.dto;

import com.sergiocanzoneri.petapi.model.Pet;

// DTO to be used as response body for requests that return one or more pets.
public record PetOutDto (
        Long id,
        String name,
        String species,
        Integer age,
        String ownerName
) {
    // Converts a domain model to a PetOutDto
    public static PetOutDto from(Pet pet) {
        return new PetOutDto(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getAge(),
                pet.getOwnerName()
        );
    }
}
