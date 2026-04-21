package com.example.demo.service;

import com.example.demo.dto.trainer.TrainerRequestDto;
import com.example.demo.dto.trainer.TrainerResponseDto;
import com.example.demo.enums.RentalStatus;
import com.example.demo.exceptions.DuplicateTrainerEmailException;
import com.example.demo.exceptions.TrainerNotFoundException;
import com.example.demo.exceptions.TrainerWithActiveRentalDeleteException;
import com.example.demo.models.Trainer;
import com.example.demo.repository.RentalRepository;
import com.example.demo.repository.TrainerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final RentalRepository rentalRepository;

    public TrainerService(TrainerRepository trainerRepository, RentalRepository rentalRepository) {
        this.trainerRepository = trainerRepository;
        this.rentalRepository = rentalRepository;
    }

    public List<TrainerResponseDto> findAll() {
        return trainerRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public TrainerResponseDto findById(Long id) {
        Trainer trainer = getTrainerEntityById(id);
        return mapToDto(trainer);
    }

    public TrainerResponseDto create(TrainerRequestDto requestDto) {
        if (trainerRepository.existsByEmailIgnoreCase(requestDto.email())) {
            throw new DuplicateTrainerEmailException(requestDto.email());
        }

        Trainer trainer = new Trainer();
        trainer.setFirstName(requestDto.firstName());
        trainer.setLastName(requestDto.lastName());
        trainer.setEmail(requestDto.email());

        trainer = trainerRepository.save(trainer);
        return mapToDto(trainer);
    }

    public TrainerResponseDto update(Long id, TrainerRequestDto requestDto) {
        Trainer trainer = getTrainerEntityById(id);

        boolean duplicateEmailExists = trainerRepository.findByEmailIgnoreCase(requestDto.email())
                .map(foundTrainer -> !foundTrainer.getId().equals(id))
                .orElse(false);

        if (duplicateEmailExists) {
            throw new DuplicateTrainerEmailException(requestDto.email());
        }

        trainer.setFirstName(requestDto.firstName());
        trainer.setLastName(requestDto.lastName());
        trainer.setEmail(requestDto.email());

        trainer = trainerRepository.save(trainer);
        return mapToDto(trainer);
    }

    public void delete(Long id) {
        Trainer trainer = getTrainerEntityById(id);

        boolean hasActiveRentals = rentalRepository.existsByTrainerIdAndStatus(id, RentalStatus.ACTIVE);

        if (hasActiveRentals) {
            throw new TrainerWithActiveRentalDeleteException(
                    "Nie można usunąć trenera z aktywnym wypożyczeniem"
            );
        }

        trainerRepository.delete(trainer);
    }

    public Trainer getTrainerEntityById(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new TrainerNotFoundException(id));
    }

    private TrainerResponseDto mapToDto(Trainer trainer) {
        return new TrainerResponseDto(
                trainer.getId(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getEmail()
        );
    }
}