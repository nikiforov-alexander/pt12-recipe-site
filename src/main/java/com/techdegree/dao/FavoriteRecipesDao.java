package com.techdegree.dao;

import com.techdegree.model.Recipe;
import com.techdegree.model.User;

import java.util.List;

public interface FavoriteRecipesDao {
    List<Recipe> findAllFavoriteRecipesFor(User user);

    void addFavoriteRecipeForUser(Long recipeId, Long userId);

    void removeFavoriteRecipeForUser(Long recipeId, Long userId);

    List<String> findStepsForRecipe(Long recipeId);
}
