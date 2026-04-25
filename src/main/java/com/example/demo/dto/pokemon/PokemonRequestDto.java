package com.example.demo.dto.pokemon;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PokemonRequestDto(
        @NotBlank(message = "Pokemon name is required")
        String name,

        @NotBlank(message = "Pokemon type is required")
        String type,

        @Min(value = 1, message = "Level must be at least 1")
        @Max(value = 100, message = "Level cannot be greater than 100")
        int level,

        @Min(value = 1, message = "HP must be greater than 0")
        int hp
) {}
