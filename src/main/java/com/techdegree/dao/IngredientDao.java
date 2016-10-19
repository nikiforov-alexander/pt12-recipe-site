package com.techdegree.dao;

import com.techdegree.model.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

// TODO : test all these methods and add other delete and save methods
@Repository
public interface IngredientDao extends CrudRepository<Ingredient, Long> {
    // ingredient can be saved and/or deleted in REST API
    // only if user is owner of recipe, or admin

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "#ingredient.recipe.owner == authentication.principal")
    void delete(@Param("ingredient") Ingredient ingredient);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "#ingredient.recipe.owner == authentication.principal")
    Ingredient save(@Param("ingredient") Ingredient ingredient);
}
