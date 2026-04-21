package com.example.demo.repository;

import com.example.demo.enums.PokemonStatus;
import com.example.demo.models.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long>, JpaSpecificationExecutor<Pokemon> {
    Optional<Pokemon> findByNameIgnoreCase(String name);

    List<Pokemon> findByIdAndStatus(Long id, PokemonStatus status);
    List<Pokemon> findByTypeIgnoreCase(String type);
    List<Pokemon> findByLevelBetween(Integer minLevel, Integer maxLevel);



}