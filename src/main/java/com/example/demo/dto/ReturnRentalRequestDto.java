package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public record ReturnRentalRequestDto(
        @NotNull(message = "Pole injured jest wymagane")
        Boolean injured
) {
}