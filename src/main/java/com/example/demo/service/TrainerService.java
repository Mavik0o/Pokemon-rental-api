package com.example.demo.service;

import com.example.demo.models.Trainer;
import com.example.demo.repository.TrainerRepository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public class TrainerService {
    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public List<Trainer> findAll() {
        return this.trainerRepository.findAll();
    }

    public Trainer findById(@PathVariable Long id) {
        return this.trainerRepository.findById(id).orElse(null);
    }

    public Trainer update(Long id, Trainer trainer) {
        Trainer trainer1 = this.trainerRepository.findById(id).orElse(null);
        trainer1.setFirstName(trainer.getFirstName());
        trainer1.setLastName(trainer.getLastName());
        trainer1.setEmail(trainer.getEmail());

        return this.trainerRepository.save(trainer);
    }





}
