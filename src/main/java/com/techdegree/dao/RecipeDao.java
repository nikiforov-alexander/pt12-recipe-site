package com.techdegree.dao;

import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

// second interface that is extended
// allows us to use methods implemented in
// RecipeDaoImpl
// see Spring Data docs:
// http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.custom-implementations
// one has to be careful about naming implementation,
// MUST be RecipeDaoImpl
@Repository
public interface RecipeDao extends
        CrudRepository<Recipe, Long>,
        FavoriteRecipesDao {

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
            "#recipe.owner == authentication.principal")
    void delete(@Param("recipe") Recipe recipe);

    List<Recipe> findByRecipeCategory(RecipeCategory recipeCategory);
}
