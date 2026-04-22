package com.example.demo.models;

import com.example.demo.enums.PokemonStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pokemons")
@Getter
@Setter
@NoArgsConstructor
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String type;

    @Min(1)
    @Max(100)
    @Column(nullable = false)
    private int level;

    @Min(1)
    @Column(nullable = false)
    private int hp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PokemonStatus status;
}