package com.techdegree.service;

import com.techdegree.model.Recipe;
import com.techdegree.model.User;

import java.util.List;

public interface RecipeService {
    List<Recipe> findAll();
    Recipe findOne(Long id);
    Recipe save(Recipe recipe);
    void delete(Recipe recipe);
    List<Recipe> findFavoriteRecipesForUser(User user);
}
