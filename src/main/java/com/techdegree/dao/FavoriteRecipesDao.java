package com.techdegree.dao;

import com.techdegree.model.Recipe;
import com.techdegree.model.User;

import java.util.List;

public interface FavoriteRecipesDao {
    List<Recipe> findAllFavoriteRecipesFor(User user);
}
