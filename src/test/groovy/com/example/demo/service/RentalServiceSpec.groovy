package com.example.demo.service

import com.example.demo.dto.rental.CreateRentalRequestDto
import com.example.demo.dto.rental.RentalResponseDto
import com.example.demo.dto.rental.ReturnRentalRequestDto
import com.example.demo.enums.PokemonStatus
import com.example.demo.enums.RentalStatus
import com.example.demo.exceptions.ConflictException
import com.example.demo.exceptions.PokemonAlreadyRentedException
import com.example.demo.exceptions.PokemonInjuredException
import com.example.demo.exceptions.TrainerRentalLimitExceededException
import com.example.demo.mappers.RentalMapper
import com.example.demo.models.Pokemon
import com.example.demo.models.Rental
import com.example.demo.models.Trainer
import com.example.demo.repository.RentalRepository
import spock.lang.Specification

class RentalServiceSpec extends Specification {

    RentalRepository rentalRepository = Mock()
    TrainerService trainerService = Mock()
    PokemonService pokemonService = Mock()
    RentalMapper rentalMapper = Mock()
    RentalService rentalService = new RentalService(rentalRepository, trainerService, pokemonService, rentalMapper)

    def "create should attach relations and persist rental"() {
        given:
        def trainer = buildTrainer()
        def pokemon = buildPokemon(PokemonStatus.AVAILABLE)
        def request = new CreateRentalRequestDto(3L, 2L)
        def rental = new Rental()
        def response = new RentalResponseDto(10L, 3L, 2L, null, null, RentalStatus.ACTIVE)

        when:
        def result = rentalService.create(request)

        then:
        1 * trainerService.getTrainerEntityById(2L) >> trainer
        1 * pokemonService.getPokemonEntityById(3L) >> pokemon
        1 * rentalRepository.countByTrainerIdAndStatus(2L, RentalStatus.ACTIVE) >> 0L
        1 * rentalMapper.toEntity(request) >> rental
        1 * pokemonService.updatePokemonStatus(3L, PokemonStatus.RENTED)
        1 * rentalRepository.save(rental) >> rental
        1 * rentalMapper.toDto(rental) >> response
        rental.trainer == trainer
        rental.pokemon == pokemon
        result == response
    }

    def "create should reject rented pokemon"() {
        given:
        def request = new CreateRentalRequestDto(3L, 2L)

        when:
        rentalService.create(request)

        then:
        1 * trainerService.getTrainerEntityById(2L) >> buildTrainer()
        1 * pokemonService.getPokemonEntityById(3L) >> buildPokemon(PokemonStatus.RENTED)
        0 * rentalRepository.save(_)
        thrown(PokemonAlreadyRentedException)
    }

    def "create should reject injured pokemon"() {
        given:
        def request = new CreateRentalRequestDto(3L, 2L)

        when:
        rentalService.create(request)

        then:
        1 * trainerService.getTrainerEntityById(2L) >> buildTrainer()
        1 * pokemonService.getPokemonEntityById(3L) >> buildPokemon(PokemonStatus.INJURED)
        0 * rentalRepository.save(_)
        thrown(PokemonInjuredException)
    }

    def "create should reject when trainer reached limit"() {
        given:
        def request = new CreateRentalRequestDto(3L, 2L)

        when:
        rentalService.create(request)

        then:
        1 * trainerService.getTrainerEntityById(2L) >> buildTrainer()
        1 * pokemonService.getPokemonEntityById(3L) >> buildPokemon(PokemonStatus.AVAILABLE)
        1 * rentalRepository.countByTrainerIdAndStatus(2L, RentalStatus.ACTIVE) >> 3L
        0 * rentalRepository.save(_)
        thrown(TrainerRentalLimitExceededException)
    }

    def "findByTrainerId should validate trainer exists"() {
        given:
        def rental = buildRental()
        def response = new RentalResponseDto(10L, 3L, 2L, null, null, RentalStatus.ACTIVE)

        when:
        def result = rentalService.findByTrainerId(2L)

        then:
        1 * trainerService.getTrainerEntityById(2L) >> buildTrainer()
        1 * rentalRepository.findByTrainerId(2L) >> [rental]
        1 * rentalMapper.toDto(rental) >> response
        result.size() == 1
    }

    def "findByPokemonId should validate pokemon exists"() {
        given:
        def rental = buildRental()
        def response = new RentalResponseDto(10L, 3L, 2L, null, null, RentalStatus.ACTIVE)

        when:
        def result = rentalService.findByPokemonId(3L)

        then:
        1 * pokemonService.getPokemonEntityById(3L) >> buildPokemon(PokemonStatus.AVAILABLE)
        1 * rentalRepository.findByPokemonId(3L) >> [rental]
        1 * rentalMapper.toDto(rental) >> response
        result.size() == 1
    }

    def "returnRental should mark rental returned and pokemon injured when requested"() {
        given:
        def rental = buildRental()
        def request = new ReturnRentalRequestDto(true)
        def response = new RentalResponseDto(10L, 3L, 2L, null, null, RentalStatus.RETURNED)

        when:
        def result = rentalService.returnRental(10L, request)

        then:
        1 * rentalRepository.findById(10L) >> Optional.of(rental)
        1 * pokemonService.updatePokemonStatus(3L, PokemonStatus.INJURED)
        1 * rentalRepository.save(rental) >> rental
        1 * rentalMapper.toDto(rental) >> response
        rental.status == RentalStatus.RETURNED
        rental.returnedAt != null
        result.status() == RentalStatus.RETURNED
    }

    def "cancelRental should reject inactive rental"() {
        given:
        def rental = buildRental()
        rental.status = RentalStatus.CANCELLED

        when:
        rentalService.cancelRental(10L)

        then:
        1 * rentalRepository.findById(10L) >> Optional.of(rental)
        0 * rentalRepository.save(_)
        thrown(ConflictException)
    }

    private static Trainer buildTrainer() {
        def trainer = new Trainer()
        trainer.id = 2L
        trainer.firstName = "Misty"
        trainer.lastName = "Waterflower"
        trainer.email = "misty@cerulean.test"
        trainer
    }

    private static Pokemon buildPokemon(PokemonStatus status) {
        def pokemon = new Pokemon()
        pokemon.id = 3L
        pokemon.name = "Staryu"
        pokemon.type = "Water"
        pokemon.level = 20
        pokemon.hp = 90
        pokemon.status = status
        pokemon
    }

    private static Rental buildRental() {
        def rental = new Rental()
        rental.id = 10L
        rental.trainer = buildTrainer()
        rental.pokemon = buildPokemon(PokemonStatus.AVAILABLE)
        rental.status = RentalStatus.ACTIVE
        rental
    }
}
