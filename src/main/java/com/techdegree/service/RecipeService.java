package com.techdegree.service;

import com.techdegree.model.Recipe;
import com.techdegree.model.User;

import java.util.List;

public interface RecipeService {
    List<Recipe> findAll();
    Recipe findOne(Long id);
    Recipe save(Recipe recipe, User user);
    void delete(Recipe recipe);
    List<Recipe> findFavoriteRecipesForUser(User user);
    List<Recipe> findByRecipeCategoryName(String name);
}
