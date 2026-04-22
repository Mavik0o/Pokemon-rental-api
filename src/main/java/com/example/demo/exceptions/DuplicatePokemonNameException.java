package com.example.demo.exceptions;

public class DuplicatePokemonNameException extends ConflictException{
    public DuplicatePokemonNameException(String pokemonName) {
        super("Pokemon with name " + pokemonName + " already exists");
    }
}
