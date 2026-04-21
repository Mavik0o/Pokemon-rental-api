package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TrainerRequestDto(
        @NotBlank(message = "Imię jest wymagane")
        String firstName,

        @NotBlank(message = "Nazwisko jest wymagane")
        String lastName,

        @NotBlank(message = "Email jest wymagany")
        @Email(message = "Niepoprawny format email")
        String email
) {
}