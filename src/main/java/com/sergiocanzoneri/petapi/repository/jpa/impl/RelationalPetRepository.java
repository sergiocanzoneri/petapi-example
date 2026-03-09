package com.sergiocanzoneri.petapi.repository.jpa.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.sergiocanzoneri.petapi.controller.dto.PetFilterDto;
import com.sergiocanzoneri.petapi.model.Pet;
import com.sergiocanzoneri.petapi.repository.IPetRepository;
import com.sergiocanzoneri.petapi.repository.jpa.PetJpaRepository;
import com.sergiocanzoneri.petapi.repository.jpa.entity.PetEntity;
import com.sergiocanzoneri.petapi.util.RelationalDatabaseCondition;


// Relational (JPA) implementation of IPetRepository. Currently used with H2 and PostgreSQL.
@Component
@Primary
@Conditional(RelationalDatabaseCondition.class)
public class RelationalPetRepository implements IPetRepository {

    private final PetJpaRepository jpaRepository;

    // Constructor for dependency injection
    public RelationalPetRepository(PetJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    // Find a pet by id
    @Override
    public Optional<Pet> findById(Long id) {
        return jpaRepository.findById(id).map(PetEntity::toDomain);
    }

    // Find all pets
    @Override
    public List<Pet> findAll() {
        return jpaRepository.findAll().stream()
                .map(PetEntity::toDomain)
                .collect(Collectors.toList());
    }

    // Find pets matching the optional filters
    @Override
    public List<Pet> findByFilters(PetFilterDto filter) {
        return jpaRepository.findByFilters(
                        filter.species(),
                        filter.ownerName(),
                        filter.name(),
                        filter.age(),
                        filter.minAge(),
                        filter.maxAge()).stream()
                .map(PetEntity::toDomain)
                .collect(Collectors.toList());
    }

    // Save a pet
    @Override
    public Pet save(Pet pet) {
        PetEntity entity = PetEntity.fromDomain(pet);
        PetEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    // Delete a pet by id
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    // Check if a pet exists by id
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
