package com.techdegree.service;

import com.techdegree.dao.IngredientDao;
import com.techdegree.model.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService{
    @Autowired
    private IngredientDao ingredientDao;

    @Override
    public Ingredient save(Ingredient ingredient) {
        return ingredientDao.save(ingredient);
    }

    @Override
    public Ingredient findOne(Long id) {
        return ingredientDao.findOne(id);
    }

    @Override
    public List<Ingredient> findAll() {
        return (List<Ingredient>) ingredientDao.findAll();
    }
}
