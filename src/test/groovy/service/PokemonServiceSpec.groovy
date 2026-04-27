package service

import com.example.demo.dto.pokemon.PokemonRequestDto
import com.example.demo.dto.pokemon.PokemonResponseDto
import com.example.demo.enums.PokemonStatus
import com.example.demo.exceptions.DuplicatePokemonNameException
import com.example.demo.exceptions.PokemonNotFoundException
import com.example.demo.mappers.PokemonMapper
import com.example.demo.models.Pokemon
import com.example.demo.repository.PokemonRepository
import com.example.demo.service.PokemonService
import spock.lang.Specification

class PokemonServiceSpec extends Specification{
    PokemonRepository pokemonRepository = Mock()
    PokemonMapper pokemonMapper = Mock()
    PokemonService pokemonService = new PokemonService(pokemonRepository, pokemonMapper)

    def "create should persist mapped pokemon"(){
        given:
            def request = new PokemonRequestDto("Squirtle", "Water", 1,10)
            def response = new PokemonResponseDto(1L,"Squirtle","Water",1,10,PokemonStatus.AVAILABLE)
            def pokemon = buildPokemon()
        when:
            def result = pokemonService.create(request)
        then:
            1 * pokemonRepository.findByNameIgnoreCase("Squirtle") >> Optional.empty()
            1 * pokemonMapper.toEntity(request) >> pokemon
            1 * pokemonRepository.save(pokemon) >> pokemon
            1 * pokemonMapper.toDto(pokemon) >> response
            result == response
    }

    def "create should reject duplicate name"(){
        given:
            def request = new PokemonRequestDto("Squirtle","Water",1,10)
        when:
            pokemonService.create(request)
        then:
            1 * pokemonRepository.findByNameIgnoreCase("Squirtle") >> Optional.of(buildPokemon())
            0 * pokemonRepository.save(_)
            thrown(DuplicatePokemonNameException)

    }

    def "findById should throw when pokemon does not exist"(){
        when:
            pokemonService.findById(99L)
        then:
            1 * pokemonRepository.findById(99L) >> Optional.empty()
            def ex = thrown(PokemonNotFoundException)
            ex.message.contains("99")
    }

    private static Pokemon buildPokemon(){
        def pokemon = new Pokemon()
        pokemon.id = 1L
        pokemon.name = "Squirtle"
        pokemon.type = "Water"
        pokemon.level = 1
        pokemon.hp = 10
        pokemon.status = PokemonStatus.AVAILABLE

        pokemon
    }

}
