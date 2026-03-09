package com.sergiocanzoneri.petapi.repository.nosql.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import com.sergiocanzoneri.petapi.controller.dto.PetFilterDto;
import com.sergiocanzoneri.petapi.model.Pet;
import com.sergiocanzoneri.petapi.repository.IPetRepository;
import com.sergiocanzoneri.petapi.util.NoSqlDatabaseCondition;

// TODO: To be replaced by a real non-relational db. Temporary stub implementation.
// Stub implementation of IPetRepository for non-relational (document/key-value) databases.
@Component
@Conditional(NoSqlDatabaseCondition.class)
public class NoSqlPetRepository implements IPetRepository {

    // In-memory store for the pets
    private final ConcurrentHashMap<Long, Pet> store = new ConcurrentHashMap<>();

    // Id generator for the pets
    private final AtomicLong idGenerator = new AtomicLong(1L);

    // Find a pet by id
    @Override
    public Optional<Pet> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    // Find all pets
    @Override
    public List<Pet> findAll() {
        return new ArrayList<>(store.values());
    }

    // Find pets matching the optional filters
    @Override
    public List<Pet> findByFilters(PetFilterDto filter) {
        return store.values().stream()
                .filter(pet -> Objects.isNull(filter.species()) || filter.species().equals(pet.getSpecies()))
                .filter(pet -> Objects.isNull(filter.ownerName()) || filter.ownerName().equals(pet.getOwnerName()))
                .filter(pet -> Objects.isNull(filter.name()) || filter.name().equals(pet.getName()))
                .filter(pet -> matchesAgeFilter(pet.getAge(), filter))
                .toList();
    }

    private static boolean matchesAgeFilter(int petAge, PetFilterDto filter) {
        if (filter.hasExactAge()) {
            return filter.age().equals(petAge);
        }
        if (filter.hasRangeAge()) {
            boolean aboveMin = Objects.isNull(filter.minAge()) || petAge >= filter.minAge();
            boolean belowMax = Objects.isNull(filter.maxAge()) || petAge <= filter.maxAge();
            return aboveMin && belowMax;
        }
        return true;
    }

    // Save a pet
    @Override
    public Pet save(Pet pet) {
        Long id = pet.getId();
        if (Objects.isNull(id)) {
            id = idGenerator.getAndIncrement();
            pet = new Pet(id, pet.getName(), pet.getSpecies(), pet.getAge(), pet.getOwnerName());
        }
        store.put(id, pet);
        return pet;
    }

    // Delete a pet by id
    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    // Check if a pet exists by id
    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }
}
