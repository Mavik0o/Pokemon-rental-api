package com.example.demo.integration

import com.example.demo.enums.PokemonStatus
import com.example.demo.models.Pokemon
import com.example.demo.models.Trainer
import com.example.demo.repository.PokemonRepository
import com.example.demo.repository.TrainerRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@Transactional
class PokemonControllerIntegrationSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper()
    MockMvc mockMvc

    @Autowired
    PokemonRepository pokemonRepository

    @Autowired
    TrainerRepository trainerRepository

    @Autowired
    WebApplicationContext webApplicationContext

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    def "create get and filter should work end to end"() {
        given:
        def request = [name: "Pikachu", type: "Electric", level: 25, hp: 100]

        expect:
        mockMvc.perform(post("/api/pokemons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.id').isNumber())
                .andExpect(jsonPath('$.name').value("Pikachu"))
                .andExpect(jsonPath('$.status').value("AVAILABLE"))

        and:
        mockMvc.perform(get("/api/pokemons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))

        and:
        mockMvc.perform(get("/api/pokemons").param("type", "elect"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].name').value("Pikachu"))
    }

    def "create should reject duplicate pokemon name"() {
        given:
        pokemonRepository.save(buildPokemon("Pikachu", PokemonStatus.AVAILABLE))
        def request = [name: "Pikachu", type: "Electric", level: 30, hp: 110]

        expect:
        mockMvc.perform(post("/api/pokemons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath('$.message').value("Pokemon with name Pikachu already exists"))
    }

    def "create should return validation errors for invalid payload"() {
        given:
        def request = [name: "", type: "", level: 0, hp: 0]

        expect:
        mockMvc.perform(post("/api/pokemons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.message').value("Validation failed"))
                .andExpect(jsonPath('$.errors.name').exists())
                .andExpect(jsonPath('$.errors.type').exists())
                .andExpect(jsonPath('$.errors.level').exists())
                .andExpect(jsonPath('$.errors.hp').exists())
    }

    def "heal should update injured pokemon"() {
        given:
        def pokemon = pokemonRepository.save(buildPokemon("Bulbasaur", PokemonStatus.INJURED))

        expect:
        mockMvc.perform(patch("/api/pokemons/{id}/heal", pokemon.id))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.status').value("AVAILABLE"))
    }

    def "filter should reject invalid ranges"() {
        expect:
        mockMvc.perform(get("/api/pokemons")
                .param("minLevel", "30")
                .param("maxLevel", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.message').value("Validation failed"))
                .andExpect(jsonPath('$.errors.minLevel').value(containsString("Min level")))
    }

    def "delete should reject pokemon with active rental even if status is available"() {
        given:
        def pokemon = pokemonRepository.save(buildPokemon("Charmander", PokemonStatus.AVAILABLE))
        def trainer = new Trainer(firstName: "Ash", lastName: "Ketchum", email: "ash-delete@kanto.test")
        trainer = trainerRepository.save(trainer)

        mockMvc.perform(post("/api/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString([pokemonId: pokemon.id, trainerId: trainer.id])))
                .andExpect(status().isCreated())

        pokemon.status = PokemonStatus.AVAILABLE
        pokemonRepository.save(pokemon)

        expect:
        mockMvc.perform(delete("/api/pokemons/{id}", pokemon.id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath('$.message').value(("Pokemon with id ${pokemon.id} is already rented").toString()))
    }

    private static Pokemon buildPokemon(String name, PokemonStatus status) {
        new Pokemon(name: name, type: "Electric", level: 25, hp: 100, status: status)
    }
}
