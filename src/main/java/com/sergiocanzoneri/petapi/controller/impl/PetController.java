package com.sergiocanzoneri.petapi.controller.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sergiocanzoneri.petapi.controller.IPetController;
import com.sergiocanzoneri.petapi.controller.dto.PetFilterDto;
import com.sergiocanzoneri.petapi.controller.dto.PetInDto;
import com.sergiocanzoneri.petapi.controller.dto.PetOutDto;
import com.sergiocanzoneri.petapi.controller.dto.PetUpdateDto;
import com.sergiocanzoneri.petapi.model.Pet;
import com.sergiocanzoneri.petapi.service.IPetService;
import com.sergiocanzoneri.petapi.util.PetApiConstants;

import jakarta.validation.Valid;

// REST controller for managing pets.
@RestController
@RequestMapping(PetApiConstants.PETS_BASE_PATH)
public class PetController implements IPetController {

    // Service for managing pets
    private final IPetService petService;

    // Constructor for dependency injection
    @Autowired
    public PetController(IPetService petService) {
        this.petService = petService;
    }

    /*
     * Get pets with optional filters for species, owner name, name and age.
     * If no filters are provided, all pets are returned.
     */
    @Override
    @GetMapping
    public ResponseEntity<List<PetOutDto>> getPets(@Valid PetFilterDto filter) {

        List<Pet> pets = Objects.isNull(filter) || !filter.hasAnyFilter()
                ? petService.findAll()
                : petService.findByFilters(filter);

        List<PetOutDto> responseBody = pets.stream()
                .map(PetOutDto::from)
                .toList();
        return ResponseEntity.ok(responseBody);
    }

    // Get a pet by id
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PetOutDto> getPetById(@PathVariable Long id) {
        Pet pet = petService.findById(id);
        return ResponseEntity.ok(PetOutDto.from(pet));
    }

    // Create a new pet
    @Override
    @PostMapping
    public ResponseEntity<PetOutDto> createPet(@Valid @RequestBody PetInDto request) {
        Pet created = petService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PetOutDto.from(created));
    }

    // Update a pet
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<PetOutDto> updatePet(@PathVariable Long id, @Valid @RequestBody PetUpdateDto request) {
        Pet updated = petService.update(id, request);
        return ResponseEntity.ok(PetOutDto.from(updated));
    }

    // Delete a pet
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
