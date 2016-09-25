package com.techdegree;

import com.techdegree.dao.*;
import com.techdegree.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
    @Autowired
    private OwnerDao ownerDao;

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


        // create ingredients with items
        Ingredient ingredient1 =
                new Ingredient(item1, "condition 1", "quantity 1");
        Ingredient ingredient2 =
                new Ingredient(item2, "condition 2", "quantity 2");

        // create steps
        Step step1 = new Step("step 1");
        Step step2 = new Step("step 2");

        // create recipe
        Recipe recipe = new Recipe(
                "recipe 1",
                "Description 1",
                RecipeCategory.BREAKFAST,
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
        // set recipe owner
        Owner owner = new Owner();
        owner.setUser(userDao.findByUsername("jd"));
        // save owner first
        ownerDao.save(owner);
        // set owner to recipe, because we need for
        // owner to exist before being added to
        // recipe
        recipe.setOwner(ownerDao.findOne(1L));

        // save recipe
        recipeDao.save(recipe);

        // save ingredients and steps, otherwise they are not
        // saved with recipes
        ingredientDao.save(ingredient1);
        ingredientDao.save(ingredient2);
        stepDao.save(step1);
        stepDao.save(step2);
    }
}
