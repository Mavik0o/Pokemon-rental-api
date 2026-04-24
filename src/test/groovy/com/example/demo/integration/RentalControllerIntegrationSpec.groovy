package com.example.demo.integration

import com.example.demo.enums.PokemonStatus
import com.example.demo.enums.RentalStatus
import com.example.demo.models.Pokemon
import com.example.demo.models.Rental
import com.example.demo.models.Trainer
import com.example.demo.repository.PokemonRepository
import com.example.demo.repository.RentalRepository
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

import java.time.LocalDateTime

import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@Transactional
class RentalControllerIntegrationSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper()
    MockMvc mockMvc

    @Autowired
    TrainerRepository trainerRepository

    @Autowired
    PokemonRepository pokemonRepository

    @Autowired
    RentalRepository rentalRepository

    @Autowired
    WebApplicationContext webApplicationContext

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    def "create rental should persist and mark pokemon as rented"() {
        given:
        def trainer = saveTrainer("Brock", "brock@pewter.test")
        def pokemon = savePokemon("Onix", PokemonStatus.AVAILABLE)
        def request = [pokemonId: pokemon.id, trainerId: trainer.id]

        expect:
        mockMvc.perform(post("/api/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.pokemonId').value(pokemon.id))
                .andExpect(jsonPath('$.trainerId').value(trainer.id))
                .andExpect(jsonPath('$.status').value("ACTIVE"))

        and:
        pokemonRepository.findById(pokemon.id).orElseThrow().status == PokemonStatus.RENTED
        rentalRepository.findByTrainerId(trainer.id).size() == 1
    }

    def "create rental should reject injured pokemon"() {
        given:
        def trainer = saveTrainer("Brock", "brock@pewter.test")
        def pokemon = savePokemon("Geodude", PokemonStatus.INJURED)

        expect:
        mockMvc.perform(post("/api/rentals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString([pokemonId: pokemon.id, trainerId: trainer.id])))
                .andExpect(status().isConflict())
                .andExpect(jsonPath('$.message').value(("Pokemon with id ${pokemon.id} is injured and cannot be rented").toString()))
    }

    def "return rental should update rental and pokemon"() {
        given:
        def trainer = saveTrainer("Tracey", "tracey@orange.test")
        def pokemon = savePokemon("Scyther", PokemonStatus.RENTED)
        def rental = rentalRepository.save(new Rental(trainer: trainer, pokemon: pokemon, status: RentalStatus.ACTIVE, rentedAt: LocalDateTime.now().minusDays(1)))

        expect:
        mockMvc.perform(patch("/api/rentals/{id}/return", rental.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString([injured: false])))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.status').value("RETURNED"))
                .andExpect(jsonPath('$.returnedAt').exists())

        and:
        rentalRepository.findById(rental.id).orElseThrow().status == RentalStatus.RETURNED
        rentalRepository.findById(rental.id).orElseThrow().returnedAt != null
        pokemonRepository.findById(pokemon.id).orElseThrow().status == PokemonStatus.AVAILABLE
    }

    def "get rentals by status should filter results"() {
        given:
        def trainer = saveTrainer("Gary", "gary@oak.test")
        def activePokemon = savePokemon("Eevee", PokemonStatus.RENTED)
        def returnedPokemon = savePokemon("Squirtle", PokemonStatus.AVAILABLE)

        rentalRepository.save(new Rental(trainer: trainer, pokemon: activePokemon, status: RentalStatus.ACTIVE, rentedAt: LocalDateTime.now().minusHours(2)))
        rentalRepository.save(new Rental(trainer: trainer, pokemon: returnedPokemon, status: RentalStatus.RETURNED, rentedAt: LocalDateTime.now().minusDays(2), returnedAt: LocalDateTime.now().minusDays(1)))

        expect:
        mockMvc.perform(get("/api/rentals").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$', hasSize(1)))
                .andExpect(jsonPath('$[0].status').value("ACTIVE"))
    }

    def "get rentals by trainer should return not found for missing trainer"() {
        expect:
        mockMvc.perform(get("/api/rentals/trainer/{trainerId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath('$.message').value("Trainer with id 999 not found"))
    }

    def "get rentals by pokemon should return not found for missing pokemon"() {
        expect:
        mockMvc.perform(get("/api/rentals/pokemon/{pokemonId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath('$.message').value("Pokemon with id 999 not found"))
    }

    def "get rentals should reject invalid status parameter"() {
        expect:
        mockMvc.perform(get("/api/rentals").param("status", "BROKEN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.message').value("Invalid value for parameter 'status'"))
    }

    private Trainer saveTrainer(String firstName, String email) {
        trainerRepository.save(new Trainer(firstName: firstName, lastName: "Trainer", email: email))
    }

    private Pokemon savePokemon(String name, PokemonStatus status) {
        pokemonRepository.save(new Pokemon(name: name, type: "Rock", level: 20, hp: 120, status: status))
    }
}
