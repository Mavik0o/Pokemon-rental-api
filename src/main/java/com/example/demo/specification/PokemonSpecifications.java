package com.example.demo.specification;

import com.example.demo.enums.PokemonStatus;
import com.example.demo.models.Pokemon;
import org.springframework.data.jpa.domain.Specification;

public class PokemonSpecifications {
    public static Specification<Pokemon> searchByName(String name) {
        final String nameValue = name;
        return ((root, query, criteriaBuilder) ->
                nameValue == null || nameValue.isBlank() ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + nameValue.toLowerCase() + "%"));

    }

    public static Specification<Pokemon> searchByType(String type) {
        final String typeValue = type;
        return ((root, query, criteriaBuilder) ->
                typeValue == null || typeValue.isBlank() ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), "%" + typeValue.toLowerCase() + "%"));
    }

    public static Specification<Pokemon> hasLevelGreaterThanOrEqual(Integer minLevel) {
        final Integer levelValue = minLevel;
        return ((root, query, criteriaBuilder) ->
                levelValue == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("level"), levelValue));
    }

    public static Specification<Pokemon> hasLevelLessThanOrEqual(Integer maxLevel) {
        final Integer levelValue = maxLevel;
        return ((root, query, criteriaBuilder) ->
                levelValue == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("level"), levelValue));
    }

    public static Specification<Pokemon> hasHpGreaterThanOrEqual(Integer minHp) {
        final Integer hpValue = minHp;
        return ((root, query, criteriaBuilder) ->
                hpValue == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("hp"), hpValue));
    }

    public static Specification<Pokemon> hasHpLessThanOrEqual(Integer maxHp) {
        final Integer hpValue = maxHp;
        return ((root, query, criteriaBuilder) ->
                hpValue == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("hp"), hpValue));
    }

    public static Specification<Pokemon> searchByStatus(PokemonStatus status) {
        final PokemonStatus statusValue = status;
        return ((root, query, criteriaBuilder) ->
                statusValue == null ? null : criteriaBuilder.equal(root.get("status"), statusValue));
    }
    public static Specification<Pokemon> searchById(Long id) {
        final Long idValue = id;
        return ((root, query, criteriaBuilder) ->
                idValue == null ? null : criteriaBuilder.equal(root.get("id"), idValue));
    }
}
