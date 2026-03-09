package com.sergiocanzoneri.petapi.repository;

import java.util.List;
import java.util.Optional;

import com.sergiocanzoneri.petapi.controller.dto.PetFilterDto;
import com.sergiocanzoneri.petapi.model.Pet;

// Common interface for pet persistence, independent of the underlying db type.
public interface IPetRepository {

    // Find a pet by id
    Optional<Pet> findById(Long id);

    // Find all pets
    List<Pet> findAll();

    // Save a pet
    Pet save(Pet pet);

    // Delete a pet by id
    void deleteById(Long id);

    // Check if a pet exists by id
    boolean existsById(Long id);

    // Find pets matching the optional filters.
    List<Pet> findByFilters(PetFilterDto filter);
}
