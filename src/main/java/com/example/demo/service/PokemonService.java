package com.example.demo.service;

import com.example.demo.enums.PokemonStatus;
import com.example.demo.models.Pokemon;
import com.example.demo.repository.PokemonRepository;
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

    public Pokemon findById(Long id) {
        return this.pokemonRepository.findById(id).orElse(null);
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
        if(pokemon.getStatus() == PokemonStatus.RENTED) {
            throw new IllegalStateException("Ten pokemon jest wypozyczony, nie wolno go usunac");
        }
        this.pokemonRepository.delete(pokemon);
    }
}
