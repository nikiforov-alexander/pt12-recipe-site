package com.techdegree.dao;

import com.techdegree.model.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientDao extends CrudRepository<Ingredient, Long> {

}
