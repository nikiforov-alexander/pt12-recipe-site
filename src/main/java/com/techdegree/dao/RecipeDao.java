package com.techdegree.dao;

import com.techdegree.model.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// second interface that is extended
// allows us to use methods implemented in
// RecipeDaoImpl
// see Spring Data docs:
// http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.custom-implementations
// one has to be careful about naming implementation,
// MUST be RecipeDaoImpl
@Repository
public interface RecipeDao extends
        CrudRepository<Recipe, Long>,
        FavoriteRecipesDao {

}
