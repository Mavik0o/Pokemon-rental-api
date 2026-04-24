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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@Transactional
class TrainerControllerIntegrationSpec extends Specification {

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

    def "create trainer should persist and return payload"() {
        given:
        def request = [firstName: "Ash", lastName: "Ketchum", email: "ash@kanto.test"]

        expect:
        mockMvc.perform(post("/api/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath('$.id').isNumber())
                .andExpect(jsonPath('$.email').value("ash@kanto.test"))
    }

    def "create trainer should reject duplicate email"() {
        given:
        trainerRepository.save(new Trainer(firstName: "Ash", lastName: "Ketchum", email: "ash@kanto.test"))
        def request = [firstName: "Red", lastName: "Trainer", email: "ash@kanto.test"]

        expect:
        mockMvc.perform(post("/api/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath('$.message').value("Trainer with email ash@kanto.test already exists"))
    }

    def "delete should reject trainer with active rental"() {
        given:
        def trainer = trainerRepository.save(new Trainer(firstName: "Misty", lastName: "Waterflower", email: "misty@cerulean.test"))
        def pokemon = pokemonRepository.save(new Pokemon(name: "Staryu", type: "Water", level: 18, hp: 95, status: PokemonStatus.RENTED))
        rentalRepository.save(new Rental(trainer: trainer, pokemon: pokemon, status: RentalStatus.ACTIVE, rentedAt: LocalDateTime.now()))

        expect:
        mockMvc.perform(delete("/api/trainers/{id}", trainer.id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath('$.message').exists())
    }
}
