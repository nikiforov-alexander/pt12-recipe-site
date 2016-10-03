package com.techdegree;

import com.techdegree.dao.*;
import com.techdegree.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// I use this DataLoader to load initial data into
// app : namely one user for now, one recipe,
// that is owned by that user and is his favorite
// This class is used in all tests, and in production
// we can replace it with @TestComponent and
// avoid re-creating users
@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    private IngredientDao ingredientDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RecipeDao recipeDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private StepDao stepDao;
    @Autowired
    private UserDao userDao;

    /**
     * saves Recipe with two ingredients and steps with
     * recipeName and recipeCategory
     * @param recipeName
     * @param recipeCategory
     */
    private void saveRecipeWithTwoIngredientsAndSteps(
            String recipeName,
            RecipeCategory recipeCategory
    ) {
        // create ingredients with items
        Ingredient ingredient1 =
                new Ingredient(
                        itemDao.findOne(1L),
                        "condition 1",
                        "quantity 1");
        Ingredient ingredient2 =
                new Ingredient(
                        itemDao.findOne(1L),
                        "condition 2",
                        "quantity 2");

        // create steps
        Step step1 = new Step("step 1");
        Step step2 = new Step("step 2");

        // create recipe
        Recipe recipe = new Recipe(
                recipeName,
                "Description 1",
                recipeCategory,
                "photoUrl 1",
                "preparationTime 1",
                "cookTime 1"
        );
        // put ingredients into recipe
        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        // put steps into recipe from
        // already existing steps
        recipe.addStep(step1);
        recipe.addStep(step2);

        // set ingredient1-2.recipes to recipe
        ingredient1.setRecipe(recipe);
        ingredient2.setRecipe(recipe);
        // set step1-2.recipes to recipe
        step1.setRecipe(recipe);
        step2.setRecipe(recipe);

        // set owner to recipe
        recipe.setOwner(userDao.findByUsername("jd"));

        // add recipe to user's favorite
        recipe.getFavoriteUsers().add(userDao.findByUsername("jd"));

        // save recipe
        recipeDao.save(recipe);

        // save ingredients and steps, otherwise they are not
        // saved with recipes
        ingredientDao.save(ingredient1);
        ingredientDao.save(ingredient2);
        stepDao.save(step1);
        stepDao.save(step2);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // create roles and save
        Role role = new Role("ROLE_USER");
        roleDao.save(role);

        // create users, add roles and save
        User user = new User("John Doe", "jd", "jd");
        user.setRole(role);
        userDao.save(user);

        // create items and save items, because they
        // are for now unbound to Ingredient and
        // can exist without it
        Item item1 = new Item("item 1");
        Item item2 = new Item("item 2");
        itemDao.save(item1);
        itemDao.save(item2);

        // create recipes : one for each category
        int recipeNumber = 1;
        for (RecipeCategory category : RecipeCategory.values()) {
            saveRecipeWithTwoIngredientsAndSteps(
                    "Recipe " + recipeNumber,
                    category
            );
            recipeNumber ++;
        }

    }
}
