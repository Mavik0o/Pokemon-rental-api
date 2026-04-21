package com.example.demo.dto.rental;

import com.example.demo.enums.RentalStatus;

import java.time.LocalDateTime;

public record RentalResponseDto(
        Long id,
        Long pokemonId,
        Long trainerId,
        LocalDateTime rentedAt,
        LocalDateTime returnedAt,
        RentalStatus status
) {
}