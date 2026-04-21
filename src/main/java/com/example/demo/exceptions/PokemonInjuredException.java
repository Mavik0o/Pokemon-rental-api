package com.example.demo.exceptions;

public class PokemonInjuredException extends RuntimeException{
    public PokemonInjuredException(Long pokemonId) {
        super("Pokemon with id " + pokemonId + " is injured and cannot be rented");
    }
}
