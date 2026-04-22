package com.example.demo.exceptions;

public class RentalNotFoundException extends NotFoundException{
    public RentalNotFoundException(Long id) {
        super("Rental with id " + id + " not found");
    }
}
