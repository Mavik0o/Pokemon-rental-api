package com.example.demo.repository;

import com.example.demo.enums.RentalStatus;
import com.example.demo.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByStatus(RentalStatus status);
    List<Rental> findByTrainerId(Long trainerId);
    List<Rental> findByPokemonId(Long pokemonId);

    long countByTrainerIdAndStatus(Long trainerId, RentalStatus status);

    boolean existsByTrainerIdAndStatus(Long trainerId, RentalStatus status);
}