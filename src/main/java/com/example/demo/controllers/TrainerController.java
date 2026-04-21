package com.example.demo.controllers;

import com.example.demo.dto.trainer.TrainerRequestDto;
import com.example.demo.dto.trainer.TrainerResponseDto;
import com.example.demo.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public List<TrainerResponseDto> findAll() {
        return trainerService.findAll();
    }

    @GetMapping("/{id}")
    public TrainerResponseDto getById(@PathVariable Long id) {
        return trainerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainerResponseDto create(@RequestBody @Valid TrainerRequestDto requestDto) {
        return trainerService.create(requestDto);
    }

    @PutMapping("/{id}")
    public TrainerResponseDto update(@PathVariable Long id,
                                     @RequestBody @Valid TrainerRequestDto requestDto) {
        return trainerService.update(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        trainerService.delete(id);
    }
}