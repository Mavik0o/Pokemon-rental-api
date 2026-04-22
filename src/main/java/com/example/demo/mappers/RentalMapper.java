package com.example.demo.mappers;


import com.example.demo.dto.rental.CreateRentalRequestDto;
import com.example.demo.dto.rental.RentalResponseDto;
import com.example.demo.models.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    @Mapping(source = "pokemon.id", target = "pokemonId")
    @Mapping(source = "trainer.id", target = "trainerId")
    RentalResponseDto toDto(Rental rental);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pokemon", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "rentedAt", expression = "java(java.time.LocalDateTime.now())")
    Rental toEntity(CreateRentalRequestDto createRentalRequestDto);

}
