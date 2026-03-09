package com.sergiocanzoneri.petapi.service;

import java.util.List;

import com.sergiocanzoneri.petapi.controller.dto.PetFilterDto;
import com.sergiocanzoneri.petapi.controller.dto.PetInDto;
import com.sergiocanzoneri.petapi.controller.dto.PetUpdateDto;
import com.sergiocanzoneri.petapi.model.Pet;

// Service interface for managing pets.
public interface IPetService {

    // Find all pets
    List<Pet> findAll();

    // Find a pet by id
    Pet findById(Long id);

    // Create a new pet
    Pet create(PetInDto request);

    // Update a pet
    Pet update(Long id, PetUpdateDto request);

    // Delete a pet by id
    void deleteById(Long id);

    // Find pets matching the optional filters.
    List<Pet> findByFilters(PetFilterDto filter);
}
