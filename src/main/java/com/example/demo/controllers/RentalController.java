package com.example.demo.controllers;


import com.example.demo.enums.PokemonStatus;
import com.example.demo.models.Rental;
import com.example.demo.service.RentingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Renting")
public class RentalController {
    private final RentingService rentingService;

    public RentalController(RentingService rentingService) {
        this.rentingService = rentingService;
    }

    @GetMapping
    public List<Rental> findAll() {
        return this.rentingService.findAll();
    }




    @PostMapping
    public Rental create(@RequestBody Rental rental) {
        return this.rentingService.create(rental);
    }


}
