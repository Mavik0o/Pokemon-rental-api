package com.example.demo.exceptions;

public class TrainerRentalLimitExceededException extends ConflictException{
    public TrainerRentalLimitExceededException(Long trainerId) {
        super("Trainer with id " + trainerId + " rent limit exceeded");
    }
}
