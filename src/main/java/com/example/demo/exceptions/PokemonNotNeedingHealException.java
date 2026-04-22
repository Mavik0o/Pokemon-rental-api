package com.example.demo.exceptions;

public class PokemonNotNeedingHealException extends ConflictException{
    public PokemonNotNeedingHealException(Long pokemonId) {
        super("Pokemon with id " + pokemonId + " is not needing heal");
    }
}
