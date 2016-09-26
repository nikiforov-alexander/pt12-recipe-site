package com.techdegree.dao;

import com.techdegree.model.Recipe;
import com.techdegree.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class RecipeDaoImpl implements FavoriteRecipesDao {
    // I've no idea how does it work, but apparently
    // just specifying this guy here is enough
    // to forget about all beans
    // taken, e.g. from here
    // http://howtodoinjava.com/jpa/jpa-native-select-sql-query-example/
    @PersistenceContext
    private EntityManager entityManager;

    // here we use native SQL query to get to the
    // @ManyToMany table "users_favorite_recipes"
    // don't know the better way yet
    @Override
    @SuppressWarnings("unchecked")
    public List<Recipe> findAllFavoriteRecipesFor(User user) {
        List<Recipe> favoriteRecipes =
                entityManager.createNativeQuery(
                        "SELECT * FROM recipe " +
                                "WHERE id IN (" +
                                    "SELECT recipe_id FROM users_favorite_recipes " +
                                    "WHERE user_id = ?" +
                                ")",
                        Recipe.class
                )
                .setParameter(1, user.getId())
                .getResultList();
        return favoriteRecipes;
    }
}
