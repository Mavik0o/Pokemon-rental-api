package com.example.demo.mappers;

import com.example.demo.dto.trainer.TrainerRequestDto;
import com.example.demo.dto.trainer.TrainerResponseDto;
import com.example.demo.models.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    TrainerResponseDto toDto(Trainer trainer);

    @Mapping(target = "id", ignore = true)
    Trainer toEntity(TrainerRequestDto trainerRequestDto);
}
