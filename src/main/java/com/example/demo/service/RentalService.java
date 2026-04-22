package com.example.demo.service;

import com.example.demo.dto.rental.CreateRentalRequestDto;
import com.example.demo.dto.rental.RentalResponseDto;
import com.example.demo.dto.rental.ReturnRentalRequestDto;
import com.example.demo.enums.PokemonStatus;
import com.example.demo.enums.RentalStatus;
import com.example.demo.exceptions.PokemonAlreadyRentedException;
import com.example.demo.exceptions.PokemonInjuredException;
import com.example.demo.exceptions.RentalNotFoundException;
import com.example.demo.exceptions.TrainerRentalLimitExceededException;
import com.example.demo.models.Pokemon;
import com.example.demo.models.Rental;
import com.example.demo.models.Trainer;
import com.example.demo.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;
    private final TrainerService trainerService;
    private final PokemonService pokemonService;

    public RentalService(RentalRepository rentalRepository, TrainerService trainerService, PokemonService pokemonService) {
        this.rentalRepository = rentalRepository;
        this.trainerService = trainerService;
        this.pokemonService = pokemonService;
    }

    public List<RentalResponseDto> findAll() {
        return this.rentalRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<RentalResponseDto> findByTrainerId(Long trainerId) {
        trainerService.getTrainerEntityById(trainerId);
        return this.rentalRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<RentalResponseDto> findByPokemonId(Long pokemonId) {
        pokemonService.getPokemonEntityById(pokemonId);
        return this.rentalRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<RentalResponseDto> findByStatus(RentalStatus status) {
        return rentalRepository.findByStatus(status)
                .stream()
                .map(this::mapToDto)
                .toList();
    }


//    public long countRentalsByTrainerId(Long id) {
//        return this.rentalRepository.countByTrainerIdAndStatus(id, RentalStatus.ACTIVE);
//
//    }


    public RentalResponseDto create(CreateRentalRequestDto requestDto) {
        Trainer trainer = trainerService.getTrainerEntityById(requestDto.trainerId());
        Pokemon pokemon = pokemonService.getPokemonEntityById(requestDto.pokemonId());

        if (pokemon.getStatus() == PokemonStatus.RENTED) {
            throw new PokemonAlreadyRentedException(pokemon.getId());
        }
        if (pokemon.getStatus() == PokemonStatus.INJURED) {
            throw new PokemonInjuredException(pokemon.getId());
        }
        long activeRentals = rentalRepository.countByTrainerIdAndStatus(trainer.getId(), RentalStatus.ACTIVE);
        if (activeRentals > 3) {
            throw new TrainerRentalLimitExceededException(trainer.getId());
        }
        Rental rental = new Rental();
        rental.setTrainer(trainer);
        rental.setPokemon(pokemon);
        rental.setRentedAt(LocalDateTime.now());
        rental.setStatus(RentalStatus.ACTIVE);

        pokemon.setStatus(PokemonStatus.RENTED);
        pokemonService.updatePokemonStatus(pokemon.getId(), PokemonStatus.RENTED);

        Rental savedRental = rentalRepository.save(rental);
        return mapToDto(savedRental);
//        boolean trainerExistsAndHasThreeOrLessRentalsActive = trainerService.findByIdAndNumberOfRentals(rental.getTrainer().getId(), 3);
//        boolean activePokemonExists = pokemonService.existsByIdAndStatus(rental.getPokemon().getId(), PokemonStatus.AVAILABLE);
//        if (trainerExistsAndHasThreeOrLessRentalsActive && activePokemonExists) {
//            pokemonService.updatePokemonStatus(rental.getPokemon().getId(), PokemonStatus.RENTED);
//            return this.rentalRepository.save(rental);
//        }
//        throw new RuntimeException("Rental creation not available");
    }


    public RentalResponseDto returnRental(Long id, ReturnRentalRequestDto requestDto) {
        Rental rental = getRentalById(id);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException("You can return only active rental");
        }
        rental.setReturnedAt(LocalDateTime.now());
        rental.setStatus(RentalStatus.RETURNED);

        if (requestDto.injured()) {
            pokemonService.updatePokemonStatus(rental.getPokemon().getId(), PokemonStatus.INJURED);
        } else {
            pokemonService.updatePokemonStatus(rental.getPokemon().getId(), PokemonStatus.AVAILABLE);
        }

        Rental updatedRental = rentalRepository.save(rental);
        return mapToDto(updatedRental);
    }

    public RentalResponseDto cancelRental(Long id) {
        Rental rental = getRentalById(id);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException("You can cancel only active rental");
        }
        rental.setReturnedAt(LocalDateTime.now());
        rental.setStatus(RentalStatus.CANCELLED);
        pokemonService.updatePokemonStatus(rental.getPokemon().getId(), PokemonStatus.AVAILABLE);

        Rental updatedRental = this.rentalRepository.save(rental);
        return mapToDto(updatedRental);
    }

    public Rental getRentalById(Long id) {
        return this.rentalRepository.findById(id).orElseThrow(() -> new RentalNotFoundException(id));
    }


    private RentalResponseDto mapToDto(Rental rental) {
        return new RentalResponseDto(
                rental.getId(),
                rental.getPokemon().getId(),
                rental.getTrainer().getId(),
                rental.getRentedAt(),
                rental.getReturnedAt(),
                rental.getStatus()
        );
    }

}
