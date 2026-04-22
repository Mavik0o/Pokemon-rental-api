package com.example.demo.mappers;

import com.example.demo.dto.pokemon.PokemonRequestDto;
import com.example.demo.dto.pokemon.PokemonResponseDto;
import com.example.demo.models.Pokemon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PokemonMapper {

    PokemonResponseDto toDto(Pokemon pokemon);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "AVAILABLE")
    Pokemon toEntity(PokemonRequestDto pokemonRequestDto);


}
