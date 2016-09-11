package com.techdegree.dao;

import com.techdegree.model.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeDao extends CrudRepository<Recipe, Long> {

}
