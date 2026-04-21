package com.example.demo.dto.trainer;

public record TrainerResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}