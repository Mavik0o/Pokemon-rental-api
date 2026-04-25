package com.example.demo.dto.rental;

import jakarta.validation.constraints.NotNull;

public record ReturnRentalRequestDto(
        @NotNull(message = "Injury information is required")
        Boolean injured
) {
}
