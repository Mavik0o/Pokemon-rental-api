package com.example.demo.dto.rental;

import jakarta.validation.constraints.NotNull;

public record CreateRentalRequestDto(
        @NotNull(message = "Pokemon selection is required")
        Long pokemonId,

        @NotNull(message = "Trainer selection is required")
        Long trainerId
) {
}
