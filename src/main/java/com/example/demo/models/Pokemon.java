package com.example.demo.models;

import com.example.demo.enums.PokemonStatus;
import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name = "Pokemon")
@Data
public class Pokemon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    @Min(value = 1)
    @Max(value = 100)
    private int level;

    @Column(nullable = false)
    @Min(value = 1)
    private int hp;

    @Column(nullable = false)
    private PokemonStatus status;


}
