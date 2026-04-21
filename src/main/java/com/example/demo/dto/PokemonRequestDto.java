package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PokemonRequestDto(
        @NotBlank(message = "Nazwa Pokemona jest wymagana")
        String name,

        @NotBlank(message = "Typ Pokemona jest wymagany")
        String type,

        @Min(value = 1, message = "Level musi być co najmniej 1")
        @Max(value = 100, message = "Level może być maksymalnie 100")
        int level,

        @Min(value = 1, message = "HP musi być większe od 0")
        int hp
) {}