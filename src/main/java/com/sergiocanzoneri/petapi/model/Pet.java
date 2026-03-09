package com.sergiocanzoneri.petapi.model;

import java.util.Objects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Persistence-agnostic domain model for a Pet (for both relational and non-relational dbs).
public class Pet {

    // Unique identifier
    private Long id;

    // Name (required, must not be empty)
    @NotBlank(message = "name is required and must not be empty")
    private String name;

    // Species (required, must not be empty)
    @NotBlank(message = "species is required and must not be empty")
    private String species;

    // Age (optional, must be greater than or equal to 0 when set)
    @Min(value = 0, message = "age must be greater than or equal to 0")
    private Integer age;

    // Owner name (optional)
    private String ownerName;

    // Constructor with all fields except id
    public Pet(String name, String species, Integer age, String ownerName) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.ownerName = ownerName;
    }

    // Constructor with all fields including id
    public Pet(Long id, String name, String species, Integer age, String ownerName) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.ownerName = ownerName;
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

    // Equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
