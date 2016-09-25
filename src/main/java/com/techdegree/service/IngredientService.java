package com.techdegree.service;

import com.techdegree.model.Ingredient;

import java.util.List;

public interface IngredientService {
    Ingredient save(Ingredient ingredient);
    Ingredient findOne(Long id);
    List<Ingredient> findAll();
}
