package com.techdegree.service;

import com.techdegree.dao.RecipeDao;
import com.techdegree.model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    @Autowired
    private RecipeDao recipeDao;

    @Override
    public List<Recipe> findAll() {
        // will leave this as Florian did, definitely
        // one of TODOs to ask @christherama
        return (List<Recipe>) recipeDao.findAll();
    }

    @Override
    public Recipe findOne(Long id) {
        return recipeDao.findOne(id);
    }

    @Override
    public Recipe save(Recipe recipe) {
        return recipeDao.save(recipe);
    }

    @Override
    public void delete(Recipe recipe) {
        recipeDao.delete(recipe);
    }

    // one more candidate to include delete(Long id) ...
    // this may be added later if needed. For now it is
    // not so interesting
}
