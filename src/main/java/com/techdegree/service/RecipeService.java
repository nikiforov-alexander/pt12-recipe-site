package com.techdegree.service;

import com.techdegree.model.Recipe;
import com.techdegree.model.User;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface RecipeService {
    List<Recipe> findAll();
    Recipe findOne(Long id);
    Recipe save(Recipe recipe, User user);
    void delete(Recipe recipe);

    List<Recipe> findByRecipeCategoryName(String name);

    List<Recipe> findFavoriteRecipesForUser(User user);

    boolean updateFavoriteRecipesForUser(Recipe recipe, User user);

    boolean checkIfRecipeIsFavoriteForUser(Recipe recipe, User user);

    void checkIfUserCanEditRecipe(User user, Recipe recipe)
            throws AccessDeniedException;
}
