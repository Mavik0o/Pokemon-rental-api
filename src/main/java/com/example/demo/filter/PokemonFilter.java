package com.example.demo.filter;

import com.example.demo.enums.PokemonStatus;
import com.example.demo.validation.ValidPokemonFilter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@ValidPokemonFilter
public record PokemonFilter(Long id,
                            @Nullable
                            String name,
                            @Nullable
                            String type,

                            @Nullable
                            @Min(value = 1, message = "Min level must be at least 1")
                            Integer minLevel,
                            @Nullable
                            @Max(value = 100, message = "Max level cannot be over 100")
                            Integer maxLevel,

                            @Nullable
                            @Min(value = 1, message = "Min hp must be at least 1")
                            Integer minHp,
                            @Nullable
                            @Min(value = 1, message = "Max hp must be at least 1")
                            Integer maxHp,

                            @Nullable
                            PokemonStatus status) {


}
