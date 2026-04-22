package com.example.demo.controllers;


import com.example.demo.dto.rental.CreateRentalRequestDto;
import com.example.demo.dto.rental.RentalResponseDto;
import com.example.demo.dto.rental.ReturnRentalRequestDto;
import com.example.demo.enums.RentalStatus;
import com.example.demo.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<RentalResponseDto> findAll(@RequestParam(required = false) RentalStatus status) {
        if (status != null) {
            return rentalService.findByStatus(status);
        }
        return this.rentalService.findAll();
    }

    @GetMapping("/trainer/{trainerId}")
    public List<RentalResponseDto> findByTrainerid(@PathVariable Long trainerId){
        return this.rentalService.findByTrainerId(trainerId);
    }

    @GetMapping("/pokemon/{pokemonId}")
    public List<RentalResponseDto> findByPokemonid(@PathVariable Long pokemonId){
        return this.rentalService.findByPokemonId(pokemonId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponseDto create(@RequestBody @Valid CreateRentalRequestDto requestDto) {
        return this.rentalService.create(requestDto);
    }

    @PatchMapping("/return")
    public RentalResponseDto returnRental(@PathVariable Long id, @RequestBody @Valid ReturnRentalRequestDto requestDto) {
        return this.rentalService.returnRental(id, requestDto);
    }

    @PatchMapping("/{id}/cancel")
    public RentalResponseDto cancelRental(@PathVariable Long id) {
        return this.rentalService.cancelRental(id);
    }

}
