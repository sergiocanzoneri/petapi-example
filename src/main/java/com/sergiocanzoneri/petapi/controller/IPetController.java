package com.sergiocanzoneri.petapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sergiocanzoneri.petapi.controller.dto.PetFilterDto;
import com.sergiocanzoneri.petapi.controller.dto.PetInDto;
import com.sergiocanzoneri.petapi.controller.dto.PetOutDto;
import com.sergiocanzoneri.petapi.controller.dto.PetUpdateDto;

// Interface for the PetController
public interface IPetController {

    // Get pets with optional filters
    ResponseEntity<List<PetOutDto>> getPets(PetFilterDto filter);

    // Get a pet by id
    ResponseEntity<PetOutDto> getPetById(Long id);

    // Create a new pet
    ResponseEntity<PetOutDto> createPet(PetInDto request);

    // Update a pet
    ResponseEntity<PetOutDto> updatePet(Long id, PetUpdateDto request);

    // Delete a pet
    ResponseEntity<Void> deletePet(Long id);

}

