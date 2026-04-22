package com.example.demo.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PokemonFilterValidator.class)
public @interface ValidPokemonFilter {
    String message() default "Invalid Pokemon Filter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
