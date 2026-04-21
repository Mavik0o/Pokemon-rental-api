package com.example.demo.dto.pokemon;

import com.example.demo.enums.PokemonStatus;

public record PokemonFilter(Long id,
                            String name,
                            String type,

                            Integer minLevel,
                            Integer maxLevel,

                            Integer minHp,
                            Integer maxHp,

                            PokemonStatus status) {


}
