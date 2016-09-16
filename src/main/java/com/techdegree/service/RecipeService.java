package com.techdegree.service;

import com.techdegree.model.Recipe;

import java.util.List;

public interface RecipeService {
    List<Recipe> findAll();
    Recipe findOne(Long id);
    Recipe save(Recipe recipe);
    void delete(Recipe recipe);
}
