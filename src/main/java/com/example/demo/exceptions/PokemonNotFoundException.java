package com.example.demo.exceptions;

public class PokemonNotFoundException extends RuntimeException {
    public PokemonNotFoundException(Long id) {
        super("Pokemon with id " + id + " not found");
    }
}
