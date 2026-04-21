package com.example.demo.dto;

import com.example.demo.enums.PokemonStatus;

public record PokemonResponseDto(
        Long id,
        String name,
        String type,
        int level,
        int hp,
        PokemonStatus status
) {}