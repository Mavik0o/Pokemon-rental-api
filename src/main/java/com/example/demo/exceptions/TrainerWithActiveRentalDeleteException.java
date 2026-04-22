package com.example.demo.exceptions;

public class TrainerWithActiveRentalDeleteException extends ConflictException{
    public TrainerWithActiveRentalDeleteException(String message) {
        super(message);
    }
}
