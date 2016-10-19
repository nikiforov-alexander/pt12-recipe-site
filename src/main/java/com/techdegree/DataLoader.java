package com.techdegree;

import com.techdegree.dao.*;
import com.techdegree.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * @param recipeName : Recipe.name to be set
     * @param recipeCategory RecipeCategory to be set
     * @param owner User that will be set as Recipe.owner to recipe
     *              saved
     */
    private void saveRecipeWithTwoIngredientsAndSteps(
            String recipeName,
            RecipeCategory recipeCategory,
            User owner
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
        recipe.setOwner(owner);

        // add recipe to user's favorite
        recipe.getFavoriteUsers().add(owner);

        // save recipe
        recipeDao.save(recipe);

        // save ingredients and steps, otherwise they are not
        // saved with recipes
        ingredientDao.save(ingredient1);
        ingredientDao.save(ingredient2);
        stepDao.save(step1);
        stepDao.save(step2);
    }

    /**
     * helpful method just to "unload" {@code run} method,
     * and to separate chunk of code that does this
     * specific thing: save three users in database:
     * Two users with "ROLE_USER", first one is
     * owner to all recipes "jd".
     * Second one does not own any recipe
     * Third one is system admin with "ROLE_ADMIN"
     */
    private void createUsersAddRolesAndSave() {
        // TODO : refactor this roleDao.findOne(1L), to findByName("ROLE_USER")
        User johnDoe = new User("John Doe", "jd", "jd");
        johnDoe.setRole(roleDao.findOne(1L));
        userDao.save(johnDoe);

        User alexDoe = new User("Alex Doe", "ad", "ad");
        alexDoe.setRole(roleDao.findOne(1L));
        userDao.save(alexDoe);

        User admin = new User("Administrator", "sa", "sa");
        admin.setRole(roleDao.findOne(2L));
        userDao.save(admin);
    }

    /**
     * method authenticates user, so that when we use
     * {@code @PreAuthorize} in DAOs we can use
     * "authentication" object for security expressions
     * @param user : User to be authenticated
     */
    private void authenticateUserToSaveRecipesAndSteps(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(
                authenticationToken
        );
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // create roles and save
        Role role = new Role("ROLE_USER");
        roleDao.save(role);
        Role roleAdmin = new Role("ROLE_ADMIN");
        roleDao.save(roleAdmin);

        // create users, add roles and save
        createUsersAddRolesAndSave();

        // create items and save items, because they
        // are for now unbound to Ingredient and
        // can exist without it
        Item item1 = new Item("item 1");
        Item item2 = new Item("item 2");
        itemDao.save(item1);
        itemDao.save(item2);

        // find user "jd" and authenticate it
        User johnDoeOwnerOfRecipes = userDao.findByUsername("jd");
        authenticateUserToSaveRecipesAndSteps(
                johnDoeOwnerOfRecipes
        );

        // create recipes : one for each category
        int recipeNumber = 1;
        for (RecipeCategory category : RecipeCategory.values()) {
            saveRecipeWithTwoIngredientsAndSteps(
                    "Recipe " + recipeNumber,
                    category,
                    johnDoeOwnerOfRecipes
            );
            recipeNumber ++;
        }

    }
}
