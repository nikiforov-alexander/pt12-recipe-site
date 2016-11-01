package com.techdegree.dao;

import com.techdegree.model.Recipe;
import com.techdegree.model.User;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
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

    @Override
    public void addFavoriteRecipeForUser(Long recipeId, Long userId) {
        entityManager.createNativeQuery(
                "INSERT INTO users_favorite_recipes " +
                        "(recipe_id, user_id) " +
                        "VALUES (?, ?)")
                .setParameter(1, recipeId)
                .setParameter(2, userId)
                .executeUpdate();
    }

    @Override
    public void removeFavoriteRecipeForUser(Long recipeId, Long userId) {
        entityManager.createNativeQuery(
                "DELETE FROM users_favorite_recipes " +
                        "WHERE recipe_id = ? AND user_id = ?")
                .setParameter(1, recipeId)
                .setParameter(2, userId)
                .executeUpdate();
    }

    /**
     * finds steps for recipe with {@literal recipeId}.
     * If taken directly : recipeDao.findOne(1L).getSteps()
     * gives Lazy Instantiation problem
      * @param recipeId : recipe id, for which steps will be
     *                  printed
     * @return {@code List<String>} list of recipe.steps
     */
   @Override
    public List<String> findStepsForRecipe(Long recipeId) {
        Recipe recipe = (Recipe) entityManager.createNativeQuery(
                "SELECT * from recipe WHERE id = ?",
                Recipe.class
        ).setParameter(1, recipeId)
        .getSingleResult();
        Hibernate.initialize(recipe.getSteps());
        return recipe.getSteps();
    }
}
