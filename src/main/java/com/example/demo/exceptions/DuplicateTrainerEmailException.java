package com.example.demo.exceptions;

public class DuplicateTrainerEmailException extends ConflictException{
    public DuplicateTrainerEmailException(String trainerEmail) {
        super("Trainer with email " + trainerEmail + " already exists");
    }
}
