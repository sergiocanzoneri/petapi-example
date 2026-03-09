package com.sergiocanzoneri.petapi.repository.jpa.entity;

import com.sergiocanzoneri.petapi.model.Pet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// JPA entity for Pet (maps the domain model to the relational schema).
@Entity
@Table(name = "pets")
public class PetEntity {

    // Unique identifier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name (required, must not be empty)
    @NotBlank(message = "name is required and must not be empty")
    @Column(nullable = false)
    private String name;

    // Species (required, must not be empty)
    @NotBlank(message = "species is required and must not be empty")
    @Column(nullable = false)
    private String species;

    // Age (optional, must be greater than or equal to 0 when set)
    @Min(value = 0, message = "age must be greater than or equal to 0")
    @Column(nullable = true)
    private Integer age;

    // Owner name (optional)
    @Column(name = "owner_name", nullable = true)
    private String ownerName;

    // Converts a domain model to a JPA entity
    public static PetEntity fromDomain(Pet pet) {
        PetEntity entity = new PetEntity();
        entity.setId(pet.getId());
        entity.setName(pet.getName());
        entity.setSpecies(pet.getSpecies());
        entity.setAge(pet.getAge());
        entity.setOwnerName(pet.getOwnerName());
        return entity;
    }

    // Converts a JPA entity to a domain model
    public Pet toDomain() {
        return new Pet(id, name, species, age, ownerName);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
