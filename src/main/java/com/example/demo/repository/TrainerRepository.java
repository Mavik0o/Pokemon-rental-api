package com.example.demo.repository;

import com.example.demo.models.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
    @Query("""
    SELECT t FROM Trainer t
    WHERE t.id = :trainerId AND
    (SELECT COUNT(r) FROM Rental r
     WHERE r.trainer.id = :id
     AND r.status = 'ACTIVE') <= :numberOfRentals
""")
    Trainer findByIdAndNumberOfRentals(@PathVariable Long id, int numberOfRentals);
}