package com.techdegree.web.controller;

import com.techdegree.model.*;
import com.techdegree.service.*;
import com.techdegree.web.FlashMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import static com.techdegree.web.WebConstants.RECIPES_HOME_PAGE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:./test-RecipeControllerItTest.properties")
public class RecipeControllerItTest {
    // constants

    @LocalServerPort
    private int actualPortNumber;

    private String BASE_URI;

    // mocked classes and dependencies

    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private StepService stepService;
    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Recipe testRecipeWithAllValidFields1;
    private Recipe firstRecipeFromDatabase;

    private void createTestRecipeWithAllValidFields() {
        testRecipeWithAllValidFields1 = new Recipe
                .RecipeBuilder(null) // both id and version are null
                .withVersion(null)   // so that when saved recipe is new
                .withName("name 1")
                .withDescription("description 1")
                .withRecipeCategory(RecipeCategory.BREAKFAST)
                .withPhotoUrl("photo url 1")
                .withPreparationTime("prepTime 1")
                .withCookTime("cookTime 1")
                .build();
        Ingredient testIngredient = new Ingredient(
                itemService.findOne(1L),
                "condition",
                "quantity"
        );
        testRecipeWithAllValidFields1.addIngredient(
                testIngredient
        );
        Step testStep = new Step(
                "test step"
        );
        testRecipeWithAllValidFields1.addStep(
                testStep
        );
    }

    @Before
    public void setUp() throws Exception {
        BASE_URI = "http://localhost:" + actualPortNumber;
        mockMvc =
                webAppContextSetup(webApplicationContext)
                        .build();
        createTestRecipeWithAllValidFields();
        firstRecipeFromDatabase = recipeService.findOne(1L);
    }

    // test members and methods used to generate test data:
    // both for Recipe and Item

    @Test
    public void updatingRecipeWithAllNullInvalidFieldsShouldGiveThatNumberOfErrors()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real all, DatabaseLoader is used
        // to populate data

        // When POST request updating Recipe with all
        // invalid fields is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be "/recipes/edit/1"
        // - flash message should be sent with failure status
        // - bindingResult should have 11 errors by the number of
        //  input fields:
        //  - name @NotNull, @NotEmpty
        //  - description @NotNull, @NotEmpty
        //  - recipeCategory @NotNull
        //  - photoUrl @NotNull, @NotEmpty
        //  - cookTime @NotNull, @NotEmpty
        //  - preparationTime @NotNull, @NotEmpty
        //  - ingredients[0].item.id can be null
        //  - ingredients[0].condition can be null
        //  - ingredients[0].quantity can be null
        //  - steps[0].description can be null
        mockMvc.perform(
                post(BASE_URI + "/recipes/save")
                        .param("id", "1")
                        .param("version", "0")
        ).andDo(print())
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl("/recipes/edit/1")
                )
                .andExpect(
                        flash().attribute(
                                "flash",
                                hasProperty(
                                        "status",
                                        equalTo(FlashMessage.Status.FAILURE)
                                )
                        )
                )
                .andExpect(
                        flash().attribute(
                                "org.springframework.validation.BindingResult.recipe",
                                hasProperty("fieldErrorCount",
                                        equalTo(11)
                                )
                        )
                );
    }

    @Test
    public void addingRecipeWithAllEmptyInvalidFieldsShouldGiveThatNumberOfErrors()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real all, DatabaseLoader is used
        // to populate data

        // We also load user "jd" by username to set it as owner
        // recipe. It is not needed here, but later may be ...
        // when we add admins ...
        User user = (User) userService.loadUserByUsername("jd");
        // and we calculate number of recipes before
        // request to compare later on
        int numberOfRecipeBeforeReq = recipeService.findAll().size();

        // When POST request adding new Recipe with all
        // invalid fields is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be "/recipes/add-new"
        // - flash message should be sent with failure status
        // - bindingResult should have 11 errors by the number of
        // input fields:
        mockMvc.perform(
                post(BASE_URI + "/recipes/save")
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                        user
                                )
                        )
                        .param("name", "") // @NotEmpty
                        .param("description", "") // @NotEmpty
                        .param("recipeCategory", "") // @NotEmpty
                        .param("photoUrl", "") // @NotEmpty
                        .param("cookTime", "") // @NotEmpty
                        .param("preparationTime", "") // @NotEmpty
                        .param("ingredients[0].item.id", "0") // @ValidItem
                        .param("ingredients[0].condition", "") // @NotEmpty
                        .param("ingredients[0].quantity", "") // @NotEmpty
                        .param("steps[0].description", "") // @NotEmpty
        ).andDo(print())
        .andExpect(
            status().is3xxRedirection()
        )
        .andExpect(
            redirectedUrl("/recipes/add-new")
        )
        .andExpect(
            flash().attribute(
                    "flash",
                        hasProperty(
                                "status",
                                equalTo(FlashMessage.Status.FAILURE)
                        )
            )
        )
        .andExpect(
            flash().attribute(
                "org.springframework.validation.BindingResult.recipe",
                hasProperty("fieldErrorCount",
                        equalTo(10)
                )
            )
        );
        // Assert that number of recipes stayed same:
        assertThat(
                recipeService.findAll().size(),
                is(numberOfRecipeBeforeReq)
        );
    }

    @Test
    public void updatingRecipeWithAllValidFieldsWorks()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real all, DatabaseLoader is used
        // to populate data

        // We also load non-admin user "jd" by username to set it as owner
        // recipe. It is not needed here, but later may be ...
        // when we add admins ...
        User user = (User) userService.loadUserByUsername("jd");

        // and we calculate number of
        // recipes
        // request to compare later on
        int numberOfRecipesBeforeReq = recipeService.findAll().size();

        // Add recipe to be deleted and get its id to
        // pass to request
        Long idOfNewlyAddedRecipe =
                (long) addRecipeToBeDeletedAfterwards(user);

        // When POST request for updating firstRecipeFromDatabase
        // with all correct and changed parameters is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be RECIPES_HOME_PAGE
        // - flash message should be sent with success status
        mockMvc.perform(
        post(BASE_URI + "/recipes/save")
                .param("id",
                        idOfNewlyAddedRecipe + "")
                .param("version",
                        "0")
                .param("name",
                        testRecipeWithAllValidFields1.getName())
                .param("description",
                        testRecipeWithAllValidFields1.getDescription())
                .param("recipeCategory",
                        testRecipeWithAllValidFields1.getRecipeCategory()
                                .toString())
                .param("photoUrl",
                        testRecipeWithAllValidFields1.getPhotoUrl())
                .param("cookTime",
                        testRecipeWithAllValidFields1.getCookTime())
                .param("preparationTime",
                        testRecipeWithAllValidFields1.getPreparationTime())
                // in contrast to add-new test, here we change
                // recipe.ingredients, that is why we also include
                // "id" and "version" for both recipe.steps and
                // recipe.ingredients
                // we know "id" of those, because we added recipe
                // in test before
                .param("ingredients[0].id",
                        ingredientService.findAll().size() + "")
                .param("ingredients[0].version", "0")
                .param("ingredients[0].item.id", "1")
                .param("ingredients[0].condition", "condition")
                .param("ingredients[0].quantity", "quantity")
                .param("steps[0].id",
                        stepService.findAll().size() + "")
                .param("steps[0].version", "0")
                .param("steps[0].description", "description")
        ).andDo(print())
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(RECIPES_HOME_PAGE)
                )
                .andExpect(
                        flash().attribute(
                                "flash",
                                hasProperty(
                                        "status",
                                        equalTo(FlashMessage.Status.SUCCESS)
                                )
                        )
                );
        // Assert that number of
        // recipes
        // increased by one: well they are actually did not change
        // after edit. But we calculated them before adding
        // recipe that we edit, so +1 is right number
        assertThat(
                "number of recipes increased by 1",
                recipeService.findAll().size(),
                is(numberOfRecipesBeforeReq + 1)
        );
        // Also assert that recipe has still user and
        // is equal user with which we created recipe
        assertThat(
                "recipe still has owner after edit with same id",
                recipeService.findOne(idOfNewlyAddedRecipe).getOwner(),
                is(user)
        );
    }

    @Test
    public void savingNewRecipeWithAllValidFieldsWorks()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real all, DatabaseLoader is used
        // to populate data

        // We also load non-admin user "jd" by username to set it as owner
        // recipe. It is not needed here, but later may be ...
        // when we add admins ...
        User user = (User) userService.loadUserByUsername("jd");

        // and we calculate number of
        // recipes, steps, ingredients before
        // request to compare later on
        int numberOfRecipesBeforeReq = recipeService.findAll().size();
        int numberOfIngredientsBeforeReq = ingredientService.findAll().size();
        int numberOfStepsBeforeRequest = stepService.findAll().size();

        Long idOfNewlyAddedRecipe = (long) numberOfRecipesBeforeReq + 1;

        // When POST request
        // or adding new one (because they are same)
        // with all correct parameters
        // and logged user is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be RECIPES_HOME_PAGE
        // - flash message should be sent with success status
        mockMvc.perform(
            post(BASE_URI + "/recipes/save")
                .with(
                        SecurityMockMvcRequestPostProcessors.user(
                                user
                        )
                )
                .param("name",
                        testRecipeWithAllValidFields1.getName())
                .param("description",
                        testRecipeWithAllValidFields1.getDescription())
                .param("recipeCategory",
                        testRecipeWithAllValidFields1.getRecipeCategory().toString())
                .param("photoUrl",
                        testRecipeWithAllValidFields1.getPhotoUrl())
                .param("cookTime",
                        testRecipeWithAllValidFields1.getCookTime())
                .param("preparationTime",
                        testRecipeWithAllValidFields1.getPreparationTime())
                .param("ingredients[0].item.id", "1")
                .param("ingredients[0].condition", "condition")
                .param("ingredients[0].quantity", "quantity")
                .param("steps[0].description", "description")
        ).andDo(print())
        .andExpect(
                status().is3xxRedirection()
        )
        .andExpect(
                redirectedUrl(RECIPES_HOME_PAGE)
        )
        .andExpect(
                flash().attribute(
                        "flash",
                        hasProperty(
                                "status",
                                equalTo(FlashMessage.Status.SUCCESS)
                        )
                )
        );
        // Assert that number of recipes, ingredients,
        // steps and owners increased by one
        assertThat(
                "number of recipes increased by 1",
                recipeService.findAll().size(),
                is(numberOfRecipesBeforeReq + 1)
        );
        assertThat(
                "number of ingredients increased by 1",
                ingredientService.findAll().size(),
                is(numberOfIngredientsBeforeReq + 1)
        );
        assertThat(
                "number of steps increased by 1",
                stepService.findAll().size(),
                is(numberOfStepsBeforeRequest + 1)
        );
        assertThat(
                "recipe owner was set",
                recipeService.findOne(idOfNewlyAddedRecipe).getOwner(),
                is(user)
        );
    }

    /**
     * private method used in test below to add test recipe
     * to database, the recipe that will be deleted afterwards
     * @param user : logged user that will be set as owner
     * @return id of recipe to be deleted
     * @throws Exception because we use mockMvc and it throws
     * exception
     */
    private int addRecipeToBeDeletedAfterwards(User user) throws Exception {
        mockMvc.perform(
        post(BASE_URI + "/recipes/save")
                .with(
                        SecurityMockMvcRequestPostProcessors.user(
                                user
                        )
                )
                .param("name",
                        testRecipeWithAllValidFields1.getName())
                .param("description",
                        testRecipeWithAllValidFields1.getDescription())
                .param("recipeCategory",
                        testRecipeWithAllValidFields1.getRecipeCategory().toString())
                .param("photoUrl",
                        testRecipeWithAllValidFields1.getPhotoUrl())
                .param("cookTime",
                        testRecipeWithAllValidFields1.getCookTime())
                .param("preparationTime",
                        testRecipeWithAllValidFields1.getPreparationTime())
                .param("ingredients[0].item.id", "1")
                .param("ingredients[0].condition", "condition")
                .param("ingredients[0].quantity", "quantity")
                .param("steps[0].description", "description")
        );
        // here we check recipe with highest id, that will be the one
        // we just added
        int idOfNewlyCreatedRecipe = recipeService.findAll().size();
        Recipe recipe = recipeService.findOne(
                (long) idOfNewlyCreatedRecipe
        );
        while (recipe == null) {
            idOfNewlyCreatedRecipe--;
            recipe = recipeService.findOne(
                    (long) idOfNewlyCreatedRecipe
            );
        }
        return idOfNewlyCreatedRecipe;
    }

    @Test
    public void deletingRecipeByOwnerShouldBePossible() throws Exception {
        // Arrange:
        // mockMvc is set up as real and DatabaseLoader is used
        // to populate data

        // Arrange user:
        // We also load non-admin user "jd" by username to set it as owner
        // recipe. It is not needed here, but later may be ...
        // when we add admins ...
        User user = (User) userService.loadUserByUsername("jd");

        // Calculate number of
        // recipes, ingredients, steps
        // before request. To check consistency
        // afterwards
        int numberOfIngredientsBeforeAddingRecipeToDelete =
                ingredientService.findAll().size();
        int numberOfStepsBeforeAddingRecipeToDelete =
                stepService.findAll().size();
        int numberOfRecipesBeforeAddingRecipesToDelete =
                recipeService.findAll().size();

        // Add recipe to be deleted and get its id to
        // pass to request
        int idOfNewlyAddedRecipe = addRecipeToBeDeletedAfterwards(user);

        // When POST request to "/recipes/delete/idOfNewlyAddedRecipe"
        // is made
        // Then :
        // - status should be redirect 3xx
        // - redirected page should be RECIPES_HOME_PAGE
        // - successful flash should be sent
        mockMvc.perform(
                post("/recipes/delete/" + idOfNewlyAddedRecipe)
                .with(
                        SecurityMockMvcRequestPostProcessors.user(
                                user
                        )
                )
        ).andDo(print())
        .andExpect(
                status().is3xxRedirection()
        )
        .andExpect(
                redirectedUrl(RECIPES_HOME_PAGE)
        )
        .andExpect(
                flash().attribute(
                        "flash",
                        hasProperty(
                                "status",
                                equalTo(FlashMessage.Status.SUCCESS)
                        )
                )
        );
        // Assert that system after add and delete
        // should be as it was, i.e. number of recipes,
        // ingredients and steps added with this request
        // should stay the same
        assertThat(
                "number of recipes should be the same",
                recipeService.findAll().size(),
                is(numberOfRecipesBeforeAddingRecipesToDelete)
        );
        assertThat(
                "number of ingredients should be the same",
                ingredientService.findAll().size(),
                is(numberOfIngredientsBeforeAddingRecipeToDelete)
        );
        assertThat(
                "number of steps should be the same",
                stepService.findAll().size(),
                is(numberOfStepsBeforeAddingRecipeToDelete)
        );
    }

    @Test
    public void deletingRecipeByAdminShouldBePossible() throws Exception {
        // Arrange:
        // mockMvc is set up as real and DatabaseLoader is used
        // to populate data

        // Arrange user:
        // We also load non-admin user "jd" by username to set it as owner
        // recipe.
        User user = (User) userService.loadUserByUsername("jd");

        // Calculate number of
        // recipes, ingredients, steps
        // before request. To check consistency
        // afterwards
        int numberOfIngredientsBeforeAddingRecipeToDelete =
                ingredientService.findAll().size();
        int numberOfStepsBeforeAddingRecipeToDelete =
                stepService.findAll().size();
        int numberOfRecipesBeforeAddingRecipesToDelete =
                recipeService.findAll().size();

        // Add recipe to be deleted and get its id to
        // pass to request
        int idOfNewlyAddedRecipe = addRecipeToBeDeletedAfterwards(user);

        // When POST request to "/recipes/delete/idOfNewlyAddedRecipe"
        // with admin user is made
        // Then :
        // - status should be redirect 3xx
        // - redirected page should be RECIPES_HOME_PAGE
        // - successful flash should be sent
        mockMvc.perform(
                post("/recipes/delete/" + idOfNewlyAddedRecipe)
                        .with(
                                // log admin user
                                SecurityMockMvcRequestPostProcessors.user(
                                        userService.loadUserByUsername("sa")
                                )
                        )
        ).andDo(print())
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(RECIPES_HOME_PAGE)
                )
                .andExpect(
                        flash().attribute(
                                "flash",
                                hasProperty(
                                        "status",
                                        equalTo(FlashMessage.Status.SUCCESS)
                                )
                        )
                );
        // Assert that system after add and delete
        // should be as it was, i.e. number of recipes,
        // ingredients and steps added with this request
        // should stay the same
        assertThat(
                "number of recipes should be the same",
                recipeService.findAll().size(),
                is(numberOfRecipesBeforeAddingRecipesToDelete)
        );
        assertThat(
                "number of ingredients should be the same",
                ingredientService.findAll().size(),
                is(numberOfIngredientsBeforeAddingRecipeToDelete)
        );
        assertThat(
                "number of steps should be the same",
                stepService.findAll().size(),
                is(numberOfStepsBeforeAddingRecipeToDelete)
        );
    }

    @Test(expected = NestedServletException.class)
    public void deletingRecipeByNonAdminNonOwnerShouldThrowException()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real and DatabaseLoader is used
        // to populate data

        // Arrange user:
        // We also load non-admin user "jd" by username to set it as owner
        // recipe.
        User user = (User) userService.loadUserByUsername("jd");

        // Arrange : set up user that will make POST request
        User nonOwnerNonAdmin = (User) userService.loadUserByUsername("ad");
        assertThat(
                "user is NOT admin",
                nonOwnerNonAdmin.getRole(),
                hasProperty("name", equalTo("ROLE_USER"))
        );
        assertThat(
                "user is NOT owner",
                nonOwnerNonAdmin,
                not(
                        is(
                                recipeService.findOne(1L).getOwner()
                        )
                )
        );

        // When POST request to "/recipes/delete/idOfNewlyAddedRecipe"
        // with non-admin, not owner user is made
        // Then :
        // - status should be redirect 3xx
        // - redirected page should be RECIPES_HOME_PAGE
        // - successful flash should be sent
        mockMvc.perform(
                post("/recipes/delete/" + 1L)
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                       nonOwnerNonAdmin
                                )
                        )
        ).andDo(print());
    }
}
