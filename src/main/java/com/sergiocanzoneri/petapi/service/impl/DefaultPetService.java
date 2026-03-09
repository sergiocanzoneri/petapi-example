package com.sergiocanzoneri.petapi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sergiocanzoneri.petapi.controller.dto.PetFilterDto;
import com.sergiocanzoneri.petapi.controller.dto.PetInDto;
import com.sergiocanzoneri.petapi.controller.dto.PetUpdateDto;
import com.sergiocanzoneri.petapi.exception.InvalidFilterException;
import com.sergiocanzoneri.petapi.exception.ResourceNotFoundException;
import com.sergiocanzoneri.petapi.model.Pet;
import com.sergiocanzoneri.petapi.repository.IPetRepository;
import com.sergiocanzoneri.petapi.service.IPetService;
import com.sergiocanzoneri.petapi.util.PetApiConstants;

// Default implementation of the IPetService interface.
@Service
public class DefaultPetService implements IPetService {

    // Repository for managing pets
    private final IPetRepository petRepository;

    // Constructor for dependency injection
    @Autowired
    public DefaultPetService(IPetRepository petRepository) {
        this.petRepository = petRepository;
    }

    // Find all pets
    @Override
    @Transactional(readOnly = true)
    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    // Find a pet by id
    @Override
    @Transactional(readOnly = true)
    public Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PetApiConstants.PET_NOT_FOUND_PREFIX + id));
    }

    // Create a new pet
    @Override
    @Transactional
    public Pet create(PetInDto request) {
        Pet pet = new Pet(request.name(), request.species(), request.age(), request.ownerName());
        return petRepository.save(pet);
    }

    // Update a pet
    @Override
    @Transactional
    public Pet update(Long id, PetUpdateDto request) {
        Pet existing = findById(id);
        existing.setName(request.name());
        existing.setSpecies(request.species());
        existing.setAge(request.age());
        existing.setOwnerName(request.ownerName());
        return petRepository.save(existing);
    }

    // Delete a pet by id
    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException(PetApiConstants.PET_NOT_FOUND_PREFIX + id);
        }
        petRepository.deleteById(id);
    }

    // Find pets matching the optional filters
    @Override
    @Transactional(readOnly = true)
    public List<Pet> findByFilters(PetFilterDto filter) {
        if (filter.hasConflictingAgeFilters()) {
            throw new InvalidFilterException(PetApiConstants.INVALID_FILTER_AGE_MESSAGE);
        }
        return petRepository.findByFilters(filter);
    }
}
