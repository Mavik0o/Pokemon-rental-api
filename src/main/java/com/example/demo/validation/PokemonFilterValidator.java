package com.example.demo.validation;


import com.example.demo.filter.PokemonFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PokemonFilterValidator implements ConstraintValidator<ValidPokemonFilter, PokemonFilter> {

    @Override
    public boolean isValid(PokemonFilter pokemonFilter, ConstraintValidatorContext constraintValidatorContext) {
        if (pokemonFilter == null) {
            return true;
        }

        boolean valid = true;
        constraintValidatorContext.disableDefaultConstraintViolation();
        if (pokemonFilter.minLevel() != null && pokemonFilter.maxLevel() != null && pokemonFilter.minLevel() > pokemonFilter.maxLevel()) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Min level must be less than or equal to max level")
                    .addPropertyNode("minLevel")
                    .addConstraintViolation();
            valid = false;
        }
        if (pokemonFilter.minHp() != null && pokemonFilter.maxHp() != null && pokemonFilter.minHp() > pokemonFilter.maxHp()) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Min hp must be less than or equal to max hp")
                    .addPropertyNode("minHp")
                    .addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
