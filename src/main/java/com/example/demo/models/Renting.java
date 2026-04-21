package com.example.demo.models;

import com.example.demo.enums.PokemonStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Renting")
@Data
public class Renting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false)
    private String rentedAt;

    @Column(nullable = false)
    private String returnedAt;

    @Column(nullable = false)
    private PokemonStatus status;


}
