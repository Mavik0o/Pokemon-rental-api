package com.example.demo.exceptions;

public class PokemonAlreadyRentedException extends RuntimeException{
    public PokemonAlreadyRentedException(Long pokemonId) {
        super("Pokemon with id " + pokemonId + " is already rented");
    }
}
