package com.example.demo.controllers;


import com.example.demo.enums.PokemonStatus;
import com.example.demo.models.Rental;
import com.example.demo.service.RentingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/Renting")
public class RentingController {
    private final RentingService rentingService;

    public RentingController(RentingService rentingService) {
        this.rentingService = rentingService;
    }

    @GetMapping
    public List<Rental> findAll() {
        return this.rentingService.findAll();
    }

    @GetMapping
    public String hello(@RequestParam(value = "name", defaultValue = "World") PokemonStatus status) {
        return this.rentingService.findById(status);
    }


    @PostMapping
    public Rental create(@RequestBody Rental rental) {
        return this.rentingService.create(rental);
    }


}
