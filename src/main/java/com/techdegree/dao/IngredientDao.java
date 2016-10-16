package com.techdegree.dao;

import com.techdegree.model.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientDao extends CrudRepository<Ingredient, Long> {
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "#ingredient.recipe.owner == authentication.principal")
    Ingredient save(@Param("ingredient") Ingredient ingredient);
}
