package com.example.demo.service

import com.example.demo.dto.pokemon.PokemonRequestDto
import com.example.demo.dto.pokemon.PokemonResponseDto
import com.example.demo.enums.PokemonStatus
import com.example.demo.enums.RentalStatus
import com.example.demo.exceptions.DuplicatePokemonNameException
import com.example.demo.exceptions.PokemonAlreadyRentedException
import com.example.demo.exceptions.PokemonNotFoundException
import com.example.demo.exceptions.PokemonNotNeedingHealException
import com.example.demo.mappers.PokemonMapper
import com.example.demo.models.Pokemon
import com.example.demo.repository.PokemonRepository
import com.example.demo.repository.RentalRepository
import spock.lang.Specification

class PokemonServiceSpec extends Specification {

    PokemonRepository pokemonRepository = Mock()
    RentalRepository rentalRepository = Mock()
    PokemonMapper pokemonMapper = Mock()
    PokemonService pokemonService = new PokemonService(pokemonRepository, rentalRepository, pokemonMapper)

    def "create should persist mapped pokemon"() {
        given:
        def request = new PokemonRequestDto("Pikachu", "Electric", 25, 100)
        def pokemon = buildPokemon()
        def response = new PokemonResponseDto(1L, "Pikachu", "Electric", 25, 100, PokemonStatus.AVAILABLE)

        when:
        def result = pokemonService.create(request)

        then:
        1 * pokemonRepository.findByNameIgnoreCase("Pikachu") >> Optional.empty()
        1 * pokemonMapper.toEntity(request) >> pokemon
        1 * pokemonRepository.save(pokemon) >> pokemon
        1 * pokemonMapper.toDto(pokemon) >> response
        result == response
    }

    def "create should reject duplicate name"() {
        given:
        def request = new PokemonRequestDto("Pikachu", "Electric", 25, 100)

        when:
        pokemonService.create(request)

        then:
        1 * pokemonRepository.findByNameIgnoreCase("Pikachu") >> Optional.of(buildPokemon())
        0 * pokemonRepository.save(_)
        thrown(DuplicatePokemonNameException)
    }

    def "findById should throw when pokemon does not exist"() {
        when:
        pokemonService.findById(99L)

        then:
        1 * pokemonRepository.findById(99L) >> Optional.empty()
        def ex = thrown(PokemonNotFoundException)
        ex.message.contains("99")
    }

    def "delete should reject rented pokemon with active rental"() {
        given:
        def pokemon = buildPokemon()

        when:
        pokemonService.delete(1L)

        then:
        1 * pokemonRepository.findById(1L) >> Optional.of(pokemon)
        1 * rentalRepository.existsByPokemonIdAndStatus(1L, RentalStatus.ACTIVE) >> true
        0 * pokemonRepository.delete(_ as Pokemon)
        thrown(PokemonAlreadyRentedException)
    }

    def "delete should remove pokemon when there are no active rentals"() {
        given:
        def pokemon = buildPokemon()

        when:
        pokemonService.delete(1L)

        then:
        1 * pokemonRepository.findById(1L) >> Optional.of(pokemon)
        1 * rentalRepository.existsByPokemonIdAndStatus(1L, RentalStatus.ACTIVE) >> false
        1 * pokemonRepository.delete(pokemon)
    }

    def "heal should update status to available"() {
        given:
        def pokemon = buildPokemon()
        pokemon.status = PokemonStatus.INJURED
        def response = new PokemonResponseDto(1L, "Pikachu", "Electric", 25, 100, PokemonStatus.AVAILABLE)

        when:
        def result = pokemonService.heal(1L)

        then:
        1 * pokemonRepository.findById(1L) >> Optional.of(pokemon)
        1 * pokemonRepository.save(pokemon) >> pokemon
        1 * pokemonMapper.toDto(pokemon) >> response
        pokemon.status == PokemonStatus.AVAILABLE
        result.status() == PokemonStatus.AVAILABLE
    }

    def "heal should reject pokemon without injured status"() {
        when:
        pokemonService.heal(1L)

        then:
        1 * pokemonRepository.findById(1L) >> Optional.of(buildPokemon())
        0 * pokemonRepository.save(_)
        thrown(PokemonNotNeedingHealException)
    }

    private static Pokemon buildPokemon() {
        def pokemon = new Pokemon()
        pokemon.id = 1L
        pokemon.name = "Pikachu"
        pokemon.type = "Electric"
        pokemon.level = 25
        pokemon.hp = 100
        pokemon.status = PokemonStatus.AVAILABLE
        pokemon
    }
}
