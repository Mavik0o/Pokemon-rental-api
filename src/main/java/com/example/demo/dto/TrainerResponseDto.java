package com.example.demo.dto;

public record TrainerResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}