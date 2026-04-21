package com.example.demo.service;

import com.example.demo.dto.pokemon.PokemonFilter;
import com.example.demo.dto.pokemon.PokemonSpecifications;
import com.example.demo.enums.PokemonStatus;
import com.example.demo.exceptions.PokemonNotFoundException;
import com.example.demo.models.Pokemon;
import com.example.demo.repository.PokemonRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonService {
    private final PokemonRepository pokemonRepository;

    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public List<Pokemon> findAll() {
        return this.pokemonRepository.findAll();
    }

    public List<Pokemon> findAll(PokemonFilter filter) {
        Specification<Pokemon> spec = Specification
                .where(PokemonSpecifications.searchByName(filter.name()))
                .and(PokemonSpecifications.searchByType(filter.type()))
                .and(PokemonSpecifications.hasLevelGreaterThanOrEqual(filter.minLevel()))
                .and(PokemonSpecifications.hasLevelLessThanOrEqual(filter.maxLevel()))
                .and(PokemonSpecifications.hasHpGreaterThanOrEqual(filter.minHp()))
                .and(PokemonSpecifications.hasHpLessThanOrEqual(filter.maxHp()))
                .and(PokemonSpecifications.searchByStatus(filter.status()))
                .and(PokemonSpecifications.searchById(filter.id()));
        return pokemonRepository.findAll(spec);
    }

    public Pokemon findById(Long id) {
        return this.pokemonRepository.findById(id).orElse(null);
    }

    public boolean existsByIdAndStatus(Long id, PokemonStatus status) {
        return this.pokemonRepository.findByIdAndStatus(id, status) != null;
    }


    public Pokemon updatePokemonStatus(Long id, PokemonStatus status) {
        Pokemon pokemon = this.pokemonRepository.findById(id).orElseThrow(() -> new PokemonNotFoundException(id));
        pokemon.setStatus(status);
        return this.pokemonRepository.save(pokemon);
    }


    public Pokemon create(Pokemon pokemon) {
        return this.pokemonRepository.save(pokemon);
    }

    public Pokemon update(Long id, Pokemon updatedPokemon) {
        Pokemon pokemon = this.pokemonRepository.findById(id).orElse(null);
        pokemon.setName(updatedPokemon.getName());
        pokemon.setType(updatedPokemon.getType());

        return this.pokemonRepository.save(pokemon);
    }

    public void delete(Long id) {
        Pokemon pokemon = this.findById(id);
        if (pokemon.getStatus() == PokemonStatus.RENTED) {
            throw new IllegalStateException("Ten pokemon jest wypozyczony, nie wolno go usunac");
        }
        this.pokemonRepository.delete(pokemon);
    }
}
