package com.example.demo.controllers;

import com.example.demo.dto.pokemon.PokemonFilter;
import com.example.demo.dto.pokemon.PokemonRequestDto;
import com.example.demo.dto.pokemon.PokemonResponseDto;
import com.example.demo.service.PokemonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    public List<PokemonResponseDto> findAll(PokemonFilter filter) {
        return pokemonService.findAll(filter);
    }

    @GetMapping("/{id}")
    public PokemonResponseDto getById(@PathVariable Long id) {
        return pokemonService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PokemonResponseDto create(@RequestBody @Valid PokemonRequestDto requestDto) {
        return pokemonService.create(requestDto);
    }

    @PutMapping("/{id}")
    public PokemonResponseDto update(@PathVariable Long id,
                                     @RequestBody @Valid PokemonRequestDto requestDto) {
        return pokemonService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        pokemonService.delete(id);
    }

    @PatchMapping("/{id}/heal")
    public PokemonResponseDto heal(@PathVariable Long id) {
        return pokemonService.heal(id);
    }
}