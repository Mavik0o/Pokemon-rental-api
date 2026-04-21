package com.example.demo.repository;

import com.example.demo.models.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}