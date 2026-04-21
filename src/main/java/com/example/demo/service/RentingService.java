package com.example.demo.service;

import com.example.demo.enums.PokemonStatus;
import com.example.demo.enums.RentalStatus;
import com.example.demo.models.Rental;
import com.example.demo.repository.RentalRepository;
import com.example.demo.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class RentingService {
    private final RentalRepository rentalRepository;
    private final TrainerService trainerService;
    private final PokemonService pokemonService;
    public RentingService(RentalRepository rentalRepository, TrainerService trainerService, PokemonService pokemonService) {
        this.rentalRepository = rentalRepository;
        this.trainerService = trainerService;
        this.pokemonService = pokemonService;
    }

    public List<Rental> findAll() {
        return this.rentalRepository.findAll();
    }

    public long countRentalsByTrainerId(Long id) {
        return this.rentalRepository.countByTrainerIdAndStatus(id, RentalStatus.ACTIVE);

    }


    public Rental create(Rental rental) {
        boolean trainerExistsAndHasThreeOrLessRentalsActive = trainerService.findByIdAndNumberOfRentals(rental.getTrainer().getId(), 3);
        boolean activePokemonExists = pokemonService.existsByIdAndStatus(rental.getPokemon().getId(), PokemonStatus.AVAILABLE);
        if(trainerExistsAndHasThreeOrLessRentalsActive && activePokemonExists) {
            pokemonService.updatePokemonStatus(rental.getPokemon().getId(), PokemonStatus.RENTED);
            return this.rentalRepository.save(rental);
        }
        throw new RuntimeException("Rental creation not available");
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
