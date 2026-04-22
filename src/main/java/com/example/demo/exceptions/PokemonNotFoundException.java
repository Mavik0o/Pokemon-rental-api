package com.example.demo.exceptions;

public class PokemonNotFoundException extends NotFoundException {
    public PokemonNotFoundException(Long id) {
        super("Pokemon with id " + id + " not found");
    }
}
