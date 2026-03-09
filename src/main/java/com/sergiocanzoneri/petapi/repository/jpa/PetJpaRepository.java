package com.sergiocanzoneri.petapi.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sergiocanzoneri.petapi.repository.jpa.entity.PetEntity;

// Spring Data JPA repository for PetEntity.
@Repository
public interface PetJpaRepository extends JpaRepository<PetEntity, Long> {

    // Find pets by species
    List<PetEntity> findBySpecies(String species);

    // Find pets by owner name
    List<PetEntity> findByOwnerName(String ownerName);

    // Find pets with optional filters. When a parameter is null, it is ignored.
    // Age: either exact (age) or range (minAge/maxAge); service ensures they are not both set.
    @Query("""
            select p from PetEntity p
            where (:species is null or p.species = :species)
              and (:ownerName is null or p.ownerName = :ownerName)
              and (:name is null or p.name = :name)
              and (
                (:age is not null and p.age = :age)
                or
                (:age is null and (:minAge is null or p.age >= :minAge) and (:maxAge is null or p.age <= :maxAge))
              )
            """)
    List<PetEntity> findByFilters(
            @Param("species") String species,
            @Param("ownerName") String ownerName,
            @Param("name") String name,
            @Param("age") Integer age,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge);
}
