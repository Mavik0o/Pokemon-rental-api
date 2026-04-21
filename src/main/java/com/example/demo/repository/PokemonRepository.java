package com.example.demo.repository;

import com.example.demo.enums.PokemonStatus;
import com.example.demo.models.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    Optional<Pokemon> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    List<Pokemon> findByStatus(PokemonStatus status);
    List<Pokemon> findByTypeIgnoreCase(String type);
    List<Pokemon> findByLevelBetween(Integer minLevel, Integer maxLevel);
}