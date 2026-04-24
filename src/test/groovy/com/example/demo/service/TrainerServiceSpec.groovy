package com.example.demo.service

import com.example.demo.dto.trainer.TrainerRequestDto
import com.example.demo.dto.trainer.TrainerResponseDto
import com.example.demo.enums.RentalStatus
import com.example.demo.exceptions.DuplicateTrainerEmailException
import com.example.demo.exceptions.TrainerNotFoundException
import com.example.demo.exceptions.TrainerWithActiveRentalDeleteException
import com.example.demo.mappers.TrainerMapper
import com.example.demo.models.Trainer
import com.example.demo.repository.RentalRepository
import com.example.demo.repository.TrainerRepository
import spock.lang.Specification

class TrainerServiceSpec extends Specification {

    TrainerRepository trainerRepository = Mock()
    RentalRepository rentalRepository = Mock()
    TrainerMapper trainerMapper = Mock()
    TrainerService trainerService = new TrainerService(trainerRepository, rentalRepository, trainerMapper)

    def "create should persist trainer"() {
        given:
        def request = new TrainerRequestDto("Ash", "Ketchum", "ash@kanto.test")
        def trainer = buildTrainer()
        def response = new TrainerResponseDto(1L, "Ash", "Ketchum", "ash@kanto.test")

        when:
        def result = trainerService.create(request)

        then:
        1 * trainerRepository.existsByEmailIgnoreCase(request.email()) >> false
        1 * trainerMapper.toEntity(request) >> trainer
        1 * trainerRepository.save(trainer) >> trainer
        1 * trainerMapper.toDto(trainer) >> response
        result == response
    }

    def "create should reject duplicate email"() {
        given:
        def request = new TrainerRequestDto("Ash", "Ketchum", "ash@kanto.test")

        when:
        trainerService.create(request)

        then:
        1 * trainerRepository.existsByEmailIgnoreCase(request.email()) >> true
        0 * trainerRepository.save(_)
        thrown(DuplicateTrainerEmailException)
    }

    def "update should throw when trainer does not exist"() {
        given:
        def request = new TrainerRequestDto("Ash", "Ketchum", "ash@kanto.test")

        when:
        trainerService.update(77L, request)

        then:
        1 * trainerRepository.findById(77L) >> Optional.empty()
        thrown(TrainerNotFoundException)
    }

    def "delete should reject trainer with active rental"() {
        given:
        def trainer = buildTrainer()

        when:
        trainerService.delete(1L)

        then:
        1 * trainerRepository.findById(1L) >> Optional.of(trainer)
        1 * rentalRepository.existsByTrainerIdAndStatus(1L, RentalStatus.ACTIVE) >> true
        0 * trainerRepository.delete(_)
        thrown(TrainerWithActiveRentalDeleteException)
    }

    def "delete should remove trainer without active rentals"() {
        given:
        def trainer = buildTrainer()

        when:
        trainerService.delete(1L)

        then:
        1 * trainerRepository.findById(1L) >> Optional.of(trainer)
        1 * rentalRepository.existsByTrainerIdAndStatus(1L, RentalStatus.ACTIVE) >> false
        1 * trainerRepository.delete(trainer)
    }

    private static Trainer buildTrainer() {
        def trainer = new Trainer()
        trainer.id = 1L
        trainer.firstName = "Ash"
        trainer.lastName = "Ketchum"
        trainer.email = "ash@kanto.test"
        trainer
    }
}
