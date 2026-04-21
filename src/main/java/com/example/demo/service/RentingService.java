package com.example.demo.service;

import com.example.demo.models.Rental;
import com.example.demo.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class RentingService {
    private final RentalRepository rentalRepository;

    public RentingService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> findAll() {
        return this.rentalRepository.findAll();
    }


    public Rental create(Rental rental) {
        return this.rentalRepository.save(rental);
    }

    public Rental update(Long id, Rental rental) {
        Rental rental1 = this.rentalRepository.findById(id).orElse(null);
        rental1.setPokemon(rental.getPokemon());
        rental1.setTrainer(rental.getTrainer());
        rental1.setRentedAt(rental.getRentedAt());
        rental1.setReturnedAt(rental.getReturnedAt());
        rental1.setStatus(rental.getStatus());

        return this.rentalRepository.save(rental1);
    }

    public void delete(@PathVariable Long id) {
        Rental rental = this.rentalRepository.findById(id).orElse(null);

        this.rentalRepository.delete(rental);
    }


}
