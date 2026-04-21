package com.example.demo.exceptions;

public class TrainerRentalLimitExceededException extends RuntimeException{
    public TrainerRentalLimitExceededException(Long trainerId) {
        super("Trainer with id " + trainerId + " rent limit exceeded");
    }
}
