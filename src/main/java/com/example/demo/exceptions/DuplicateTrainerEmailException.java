package com.example.demo.exceptions;

public class DuplicateTrainerEmailException extends RuntimeException{
    public DuplicateTrainerEmailException(String trainerEmail) {
        super("Trainer with email " + trainerEmail + " already exists");
    }
}
