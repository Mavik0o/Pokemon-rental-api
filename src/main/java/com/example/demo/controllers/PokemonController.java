package com.example.demo.controllers;

import com.example.demo.dto.pokemon.PokemonFilter;
import com.example.demo.enums.PokemonStatus;
import com.example.demo.models.Pokemon;
import com.example.demo.service.PokemonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/pokemons")
public class PokemonController {
    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    public List<Pokemon> findAll(PokemonFilter filter) {
        return pokemonService.findAll(filter);
    }

    @GetMapping("/{id}")
    public Pokemon getById(@PathVariable Long id) {
        return this.pokemonService.findById(id);
    }

    @PostMapping
    public Pokemon create(@RequestBody Pokemon pokemon) {
        return this.pokemonService.create(pokemon);
    }

    @PutMapping("/{id}")
    public Pokemon update(@PathVariable Long id, @RequestBody Pokemon pokemon) {
        return this.pokemonService.update(id, pokemon);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        this.pokemonService.delete(id);
    }

}
