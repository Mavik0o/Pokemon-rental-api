package com.example.demo.controllers;

import com.example.demo.models.Trainer;
import com.example.demo.service.TrainerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/trainers")
public class TrainerController {
    private final TrainerService trainerService;
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public List<Trainer> findAll() {
        return this.trainerService.findAll();
    }
    @GetMapping("/{id}")
    public Trainer getById(@PathVariable Long id) {
        return this.trainerService.findById(id);
    }

    @PostMapping
    public Trainer create(@RequestBody Trainer trainer) {
        return this.trainerService.create(trainer);
    }

    @PutMapping("/{id}")
    public Trainer update(@PathVariable Long id, @RequestBody Trainer trainer) {
        return this.trainerService.update(id, trainer);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        this.trainerService.delete(id);
    }
}
