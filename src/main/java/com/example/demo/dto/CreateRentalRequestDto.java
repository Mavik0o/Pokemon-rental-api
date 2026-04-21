package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRentalRequestDto(
        @NotNull(message = "pokemonId jest wymagane")
        Long pokemonId,

        @NotNull(message = "trainerId jest wymagane")
        Long trainerId
) {
}