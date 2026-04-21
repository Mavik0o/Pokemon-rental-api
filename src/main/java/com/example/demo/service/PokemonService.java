package com.example.demo.service;

import com.example.demo.dto.pokemon.PokemonFilter;
import com.example.demo.dto.pokemon.PokemonRequestDto;
import com.example.demo.dto.pokemon.PokemonResponseDto;
import com.example.demo.dto.pokemon.PokemonSpecifications;
import com.example.demo.enums.PokemonStatus;
import com.example.demo.exceptions.DuplicatePokemonNameException;
import com.example.demo.exceptions.PokemonNotFoundException;
import com.example.demo.exceptions.PokemonNotNeedingHealException;
import com.example.demo.models.Pokemon;
import com.example.demo.repository.PokemonRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonService {
    private final PokemonRepository pokemonRepository;

    public PokemonService(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    public List<PokemonResponseDto> findAll(PokemonFilter filter) {
        Specification<Pokemon> spec = Specification
                .where(PokemonSpecifications.searchByName(filter.name()))
                .and(PokemonSpecifications.searchByType(filter.type()))
                .and(PokemonSpecifications.hasLevelGreaterThanOrEqual(filter.minLevel()))
                .and(PokemonSpecifications.hasLevelLessThanOrEqual(filter.maxLevel()))
                .and(PokemonSpecifications.hasHpGreaterThanOrEqual(filter.minHp()))
                .and(PokemonSpecifications.hasHpLessThanOrEqual(filter.maxHp()))
                .and(PokemonSpecifications.searchByStatus(filter.status()))
                .and(PokemonSpecifications.searchById(filter.id()));

        return pokemonRepository.findAll(spec)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public PokemonResponseDto findById(Long id) {
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new PokemonNotFoundException(id));

        return mapToDto(pokemon);
    }

    public boolean existsByIdAndStatus(Long id, PokemonStatus status) {
        return !pokemonRepository.findByIdAndStatus(id, status).isEmpty();
    }

    public Pokemon updatePokemonStatus(Long id, PokemonStatus status) {
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new PokemonNotFoundException(id));

        pokemon.setStatus(status);
        return pokemonRepository.save(pokemon);
    }

    public PokemonResponseDto create(PokemonRequestDto requestDto) {
        if (pokemonRepository.findByNameIgnoreCase(requestDto.name()).isPresent()) {
            throw new DuplicatePokemonNameException(requestDto.name());
        }

        Pokemon pokemon = new Pokemon();
        pokemon.setName(requestDto.name());
        pokemon.setType(requestDto.type());
        pokemon.setHp(requestDto.hp());
        pokemon.setLevel(requestDto.level());
        pokemon.setStatus(PokemonStatus.AVAILABLE);

        pokemon = pokemonRepository.save(pokemon);
        return mapToDto(pokemon);
    }

    public PokemonResponseDto update(Long id, PokemonRequestDto requestDto) {
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new PokemonNotFoundException(id));

        boolean duplicateNameExists = pokemonRepository.findByNameIgnoreCase(requestDto.name())
                .map(found -> !found.getId().equals(id))
                .orElse(false);

        if (duplicateNameExists) {
            throw new DuplicatePokemonNameException(requestDto.name());
        }

        pokemon.setName(requestDto.name());
        pokemon.setType(requestDto.type());
        pokemon.setLevel(requestDto.level());
        pokemon.setHp(requestDto.hp());

        pokemon = pokemonRepository.save(pokemon);
        return mapToDto(pokemon);
    }

    public void delete(Long id) {
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new PokemonNotFoundException(id));

        if (pokemon.getStatus() == PokemonStatus.RENTED) {
            throw new IllegalStateException("Ten pokemon jest wypożyczony, nie wolno go usunąć");
        }

        pokemonRepository.delete(pokemon);
    }

    public PokemonResponseDto heal(Long id) {
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new PokemonNotFoundException(id));

        if (pokemon.getStatus() != PokemonStatus.INJURED) {
            throw new PokemonNotNeedingHealException(id);
        }

        pokemon.setStatus(PokemonStatus.AVAILABLE);
        pokemon = pokemonRepository.save(pokemon);

        return mapToDto(pokemon);
    }

    private PokemonResponseDto mapToDto(Pokemon pokemon) {
        return new PokemonResponseDto(
                pokemon.getId(),
                pokemon.getName(),
                pokemon.getType(),
                pokemon.getLevel(),
                pokemon.getHp(),
                pokemon.getStatus()
        );
    }
}