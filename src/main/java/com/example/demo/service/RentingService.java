package com.example.demo.service;

import com.example.demo.enums.PokemonStatus;
import com.example.demo.models.Rental;
import com.example.demo.repository.RentingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class RentingService {
    private final RentingRepository rentingRepository;

    public RentingService(RentingRepository rentingRepository) {
        this.rentingRepository = rentingRepository;
    }

    public List<Rental> findAll() {
        return this.rentingRepository.findAll();
    }


    public Rental create(Rental rental) {
        return this.rentingRepository.save(rental);
    }

    public Rental update(Long id, Rental rental) {
        Rental rental1 = this.rentingRepository.findById(id).orElse(null);
        rental1.setPokemon(rental.getPokemon());
        rental1.setTrainer(rental.getTrainer());
        rental1.setRentedAt(rental.getRentedAt());
        rental1.setReturnedAt(rental.getReturnedAt());
        rental1.setStatus(rental.getStatus());

        return this.rentingRepository.save(rental1);
    }

    public void delete(@PathVariable Long id) {
        Rental rental = this.rentingRepository.findById(id).orElse(null);

        this.rentingRepository.delete(rental);
    }


}
