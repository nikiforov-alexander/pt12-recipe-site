package com.techdegree.service;

import com.techdegree.model.Ingredient;

public interface IngredientService {
    Ingredient save(Ingredient ingredient);
    Ingredient findOne(Long id);
}
