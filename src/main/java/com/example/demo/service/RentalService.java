package com.example.demo.service;

import com.example.demo.dto.rental.CreateRentalRequestDto;
import com.example.demo.dto.rental.RentalResponseDto;
import com.example.demo.dto.rental.ReturnRentalRequestDto;
import com.example.demo.enums.PokemonStatus;
import com.example.demo.enums.RentalStatus;
import com.example.demo.exceptions.*;
import com.example.demo.mappers.RentalMapper;
import com.example.demo.models.Pokemon;
import com.example.demo.models.Rental;
import com.example.demo.models.Trainer;
import com.example.demo.repository.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RentalService {
    private final RentalRepository rentalRepository;
    private final TrainerService trainerService;
    private final PokemonService pokemonService;
    private final RentalMapper rentalMapper;

    public RentalService(RentalRepository rentalRepository, TrainerService trainerService, PokemonService pokemonService, RentalMapper rentalMapper) {
        this.rentalRepository = rentalRepository;
        this.trainerService = trainerService;
        this.pokemonService = pokemonService;
        this.rentalMapper = rentalMapper;
    }

    public List<RentalResponseDto> findAll() {
        return this.rentalRepository.findAll()
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    public List<RentalResponseDto> findByTrainerId(Long trainerId) {
        trainerService.getTrainerEntityById(trainerId);
        return rentalRepository.findByTrainerId(trainerId)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }


    public List<RentalResponseDto> findByPokemonId(Long pokemonId) {
        pokemonService.getPokemonEntityById(pokemonId);
        return rentalRepository.findByPokemonId(pokemonId)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    public List<RentalResponseDto> findByStatus(RentalStatus status) {
        return rentalRepository.findByStatus(status)
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }


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
        if (activeRentals >= 3) {
            throw new TrainerRentalLimitExceededException(trainer.getId());
        }

        Rental rental = rentalMapper.toEntity(requestDto);
        rental.setTrainer(trainer);
        rental.setPokemon(pokemon);

        pokemonService.updatePokemonStatus(pokemon.getId(), PokemonStatus.RENTED);

        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(savedRental);
    }


    public RentalResponseDto returnRental(Long id, ReturnRentalRequestDto requestDto) {
        Rental rental = getRentalById(id);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new ConflictException("You can return only active rental");
        }
        rental.setReturnedAt(LocalDateTime.now());
        rental.setStatus(RentalStatus.RETURNED);

        if (requestDto.injured()) {
            pokemonService.updatePokemonStatus(rental.getPokemon().getId(), PokemonStatus.INJURED);
        } else {
            pokemonService.updatePokemonStatus(rental.getPokemon().getId(), PokemonStatus.AVAILABLE);
        }

        Rental updatedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(updatedRental);
    }

    public RentalResponseDto cancelRental(Long id) {
        Rental rental = getRentalById(id);

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new ConflictException("You can cancel only active rental");
        }
        rental.setReturnedAt(LocalDateTime.now());
        rental.setStatus(RentalStatus.CANCELLED);
        pokemonService.updatePokemonStatus(rental.getPokemon().getId(), PokemonStatus.AVAILABLE);

        Rental updatedRental = this.rentalRepository.save(rental);
        return rentalMapper.toDto(updatedRental);
    }

    public Rental getRentalById(Long id) {
        return this.rentalRepository.findById(id).orElseThrow(() -> new RentalNotFoundException(id));
    }


}
