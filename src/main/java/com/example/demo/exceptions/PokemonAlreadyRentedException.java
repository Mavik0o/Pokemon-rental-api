package com.example.demo.exceptions;

public class PokemonAlreadyRentedException extends ConflictException{
    public PokemonAlreadyRentedException(Long pokemonId) {
        super("Pokemon with id " + pokemonId + " is already rented");
    }
}
