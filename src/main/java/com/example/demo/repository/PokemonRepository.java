package com.example.demo.repository;


import com.example.demo.models.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    Optional<Pokemon> findByNameIgnoreCase(String name);


}
