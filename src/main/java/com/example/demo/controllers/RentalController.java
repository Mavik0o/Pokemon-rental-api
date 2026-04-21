package com.example.demo.controllers;


import com.example.demo.models.Rental;
import com.example.demo.service.RentalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/Renting")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<Rental> findAll() {
        return this.rentalService.findAll();
    }




    @PostMapping
    public Rental create(@RequestBody Rental rental) {
        return this.rentalService.create(rental);
    }


}
