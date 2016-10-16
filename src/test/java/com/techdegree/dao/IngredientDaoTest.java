package com.techdegree.dao;

import com.techdegree.model.Ingredient;
import com.techdegree.model.Recipe;
import com.techdegree.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url = jdbc:h2:./database/test-IngredientDaoTest-recipes;DB_CLOSE_ON_EXIT=FALSE"
)
public class IngredientDaoTest {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IngredientDao ingredientDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RecipeDao recipeDao;

    /**
     * Login and authorizes user by username.
     * @param username of the User to be logged in
     * @return user User that was logged
     */
    private User loginUserByUsername(String username) {
        User user = userDao.findByUsername(username);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                )
        );
        return user;
    }

    @Test
    public void ingredientCanBeCreatedByOwnerOfIngredientRecipe()
            throws Exception {
        // Arrange: get first recipe from dao
        Recipe firstRecipe = recipeDao.findOne(1L);

        // Arrange: log in user: owner
        User ownerOfIngredientRecipe = loginUserByUsername("jd");

        assertThat(
                "logged in user is owner",
                ownerOfIngredientRecipe,
                is(
                        firstRecipe.getOwner()
                )
        );

        // Arrange: create test Ingredient to be saved
        Ingredient ingredientToBeSaved =
                new Ingredient(
                        itemDao.findOne(1L), "test", "test"
                );
        ingredientToBeSaved.setRecipe(firstRecipe);

        // Act: When Ingredient is saved with recipe, that
        // is owned by ingredient.recipe
        Ingredient savedIngredient = ingredientDao.save(
                ingredientToBeSaved
        );

        // set new version and id for test recipe
        ingredientToBeSaved.setId(
                savedIngredient.getId()
        );
        ingredientToBeSaved.setVersion(
                savedIngredient.getVersion()
        );

        assertThat(
                "ingredientToBeSaved with version and id, " +
                        "should be equal to savedIngredient",
                savedIngredient,
                is(ingredientToBeSaved)
        );
    }

    @Test
    public void ingredientCanBeCreatedByAdminNonOwnerOfIngredientRecipe()
            throws Exception {
        // Arrange: get first recipe from dao
        Recipe firstRecipe = recipeDao.findOne(1L);

        // Arrange: log in user: non-owner, admin
        User admin = loginUserByUsername("sa");

        assertThat(
                "logged in user is NOT owner",
                admin,
                not(
                        is(firstRecipe.getOwner())
                )
        );
        assertThat(
                "logged in user is admin",
                admin.getRole(),
                hasProperty(
                        "name", equalTo("ROLE_ADMIN")
                )
        );

        // Arrange: create test Ingredient to be saved
        Ingredient ingredientToBeSaved =
                new Ingredient(
                        itemDao.findOne(1L), "test", "test"
                );
        ingredientToBeSaved.setRecipe(firstRecipe);

        // Act: When Ingredient is saved with recipe, that
        // is owned by ingredient.recipe
        Ingredient savedIngredient = ingredientDao.save(
                ingredientToBeSaved
        );

        // set new version and id for test recipe
        ingredientToBeSaved.setId(
                savedIngredient.getId()
        );
        ingredientToBeSaved.setVersion(
                savedIngredient.getVersion()
        );

        assertThat(
            "ingredientToBeSaved with version and id, " +
                    "should be equal to savedIngredient",
                savedIngredient,
                is(ingredientToBeSaved)
        );
    }

    @Test(expected = AccessDeniedException.class)
    public void ingredientCannotBeSavedByNonAdminNonOwnerOfIngredientRecipe()
            throws Exception {
        // Arrange: get first recipe from dao
        Recipe firstRecipe = recipeDao.findOne(1L);

        // Arrange: log in user: non-owner, non-admin
        User admin = loginUserByUsername("ad");

        assertThat(
                "logged in user is NOT owner",
                admin,
                not(
                        is(firstRecipe.getOwner())
                )
        );
        assertThat(
                "logged in user is NOT admin",
                admin.getRole(),
                hasProperty(
                        "name", equalTo("ROLE_USER")
                )
        );

        // Arrange: create test Ingredient to be saved
        Ingredient ingredientToBeSaved =
                new Ingredient(
                        itemDao.findOne(1L), "test", "test"
                );
        ingredientToBeSaved.setRecipe(firstRecipe);

        // Act: When Ingredient is saved with recipe, that
        // is owned by ingredient.recipe
        ingredientDao.save(
                ingredientToBeSaved
        );

        // Assert: AccessDeniedException should be thrown
    }
}