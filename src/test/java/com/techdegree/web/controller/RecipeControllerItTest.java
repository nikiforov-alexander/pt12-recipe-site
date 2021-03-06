package com.techdegree.web.controller;

import com.techdegree.model.*;
import com.techdegree.service.*;
import com.techdegree.web.FlashMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.Comparator;
import java.util.Optional;

import static com.techdegree.web.WebConstants.POST_SAVE_ADDRESS;
import static com.techdegree.web.WebConstants.RECIPES_HOME_PAGE;
import static com.techdegree.web.WebConstants.getDeleteRecipePageWithId;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url = jdbc:h2:./database/test-RecipeControllerItTest-recipes;DB_CLOSE_ON_EXIT=FALSE"
)
public class RecipeControllerItTest {

    // mocked classes and dependencies

    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private IngredientService ingredientService;
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
        String testStep = "test step";
        testRecipeWithAllValidFields1.addStep(
                testStep
        );
    }

    /**
     * finds maximum {@code id} value from all
     * recipes returned by {@code recipeService.findAll()}.
     * Taken from
     * http://stackoverflow.com/questions/24378646/finding-max-with-lambda-expression-in-java
     * @return {@literal Long} max value, or {@literal null}
     * otherwise
     */
    private Long getIdOfNewlyCreatedRecipe() {
        Optional<Long> maxId =
                recipeService.findAll().stream()
                .map(Recipe::getId)
                .max(
                        Comparator.comparing(i -> i)
                );
        if (maxId.isPresent()) {
            return maxId.get();
        }
        return null;
    }

    @Before
    public void setUp() throws Exception {
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

        // Given the owner of recipe will make request
        User owner = (User) userService.loadUserByUsername("jd");
        assertThat(
                "user is owner",
                owner,
                is (recipeService.findOne(1L).getOwner())
        );

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
        //  - steps can be null
        mockMvc.perform(
                post(POST_SAVE_ADDRESS)
                        .param("id", "1")
                        .param("version", "0")
                .with(
                        SecurityMockMvcRequestPostProcessors.user(
                                owner
                        )
                )
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
        // - bindingResult should have 10 errors by the number of
        // input fields:
        mockMvc.perform(
                post(POST_SAVE_ADDRESS)
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                        user
                                )
                        )
                        .param("name", "") // 1. @NotEmpty
                        .param("description", "") // 2. @NotEmpty
                        .param("recipeCategory", "") // 3. @NotEmpty
                        .param("photoUrl", "") // 4. @NotEmpty
                        .param("cookTime", "") // 5. @NotEmpty
                        .param("preparationTime", "") // 6. @NotEmpty
                        .param("ingredients[0].item.id", "0") // 7. @ValidItem
                        .param("ingredients[0].condition", "") // 8. @NotEmpty
                        .param("ingredients[0].quantity", "") // 9. @NotEmpty
                        .param("steps[0]","") // 10. @NotEmpty as part of @EachNotEmpty
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
        assertThat(
                "number of recipes in db stayed same",
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
                addRecipeToBeDeletedAfterwards(user);
        assertNotNull(
                "newly added recipe's id to be deleted should not be null",
                idOfNewlyAddedRecipe
        );

        // When POST request for updating firstRecipeFromDatabase
        // with all correct and changed parameters is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be RECIPES_HOME_PAGE
        // - flash message should be sent with success status
        mockMvc.perform(
        post(POST_SAVE_ADDRESS)
                .with(
                        SecurityMockMvcRequestPostProcessors.user(
                                user
                        )
                )
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
                // "id" and "version" for
                // recipe.ingredients
                // we know "id" of those, because we added recipe
                // in test before
                .param("ingredients[0].id",
                        ingredientService.findAll().size() + "")
                .param("ingredients[0].version", "0")
                .param("ingredients[0].item.id", "1")
                .param("ingredients[0].condition", "condition")
                .param("ingredients[0].quantity", "quantity")
                .param("steps[0]", "step 0")
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
        // recipes, ingredients before
        // request to compare later on
        int numberOfRecipesBeforeReq = recipeService.findAll().size();
        int numberOfIngredientsBeforeReq = ingredientService.findAll().size();

        // When POST request
        // or adding new one (because they are same)
        // with all correct parameters
        // and logged user is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be RECIPES_HOME_PAGE
        // - flash message should be sent with success status
        mockMvc.perform(
            post(POST_SAVE_ADDRESS)
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
                .param("steps[0]", "step 0")
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

        Long idOfNewlyAddedRecipe = getIdOfNewlyCreatedRecipe();

        assertNotNull(
                "id of newly created recipe is not null, " +
                        "otherwise our tests will not make any sense",
                idOfNewlyAddedRecipe
        );

        assertThat(
                "recipe has now one step 'step 0'",
                recipeService.findStepsForRecipe(idOfNewlyAddedRecipe),
                containsInAnyOrder("step 0")
        );
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
    private Long addRecipeToBeDeletedAfterwards(User user) throws Exception {
        mockMvc.perform(
        post(POST_SAVE_ADDRESS)
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
        );
        return getIdOfNewlyCreatedRecipe();
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
        // recipes, ingredients,
        // before request. To check consistency
        // afterwards
        int numberOfIngredientsBeforeAddingRecipeToDelete =
                ingredientService.findAll().size();
        int numberOfRecipesBeforeAddingRecipesToDelete =
                recipeService.findAll().size();

        // Add recipe to be deleted and get its id to
        // pass to request
        Long idOfNewlyAddedRecipe = addRecipeToBeDeletedAfterwards(user);
        assertNotNull(
                "newly added recipe should not be null",
                idOfNewlyAddedRecipe
        );

        // When POST request to "/recipes/delete/idOfNewlyAddedRecipe"
        // is made
        // Then :
        // - status should be redirect 3xx
        // - redirected page should be RECIPES_HOME_PAGE
        // - successful flash should be sent
        mockMvc.perform(
                post(getDeleteRecipePageWithId("" + idOfNewlyAddedRecipe))
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
        // ingredients added with this request
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
        // recipes, ingredients,
        // before request. To check consistency
        // afterwards
        int numberOfIngredientsBeforeAddingRecipeToDelete =
                ingredientService.findAll().size();
        int numberOfRecipesBeforeAddingRecipesToDelete =
                recipeService.findAll().size();

        // Add recipe to be deleted and get its id to
        // pass to request
        Long idOfNewlyAddedRecipe = addRecipeToBeDeletedAfterwards(user);
        assertNotNull(
                "id of newly added recipe should not be null",
                idOfNewlyAddedRecipe
        );

        // When POST request to "/recipes/delete/idOfNewlyAddedRecipe"
        // with admin user is made
        // Then :
        // - status should be redirect 3xx
        // - redirected page should be RECIPES_HOME_PAGE
        // - successful flash should be sent
        mockMvc.perform(
                post(getDeleteRecipePageWithId("" + idOfNewlyAddedRecipe))
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
        // ingredients added with this request
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
                post(getDeleteRecipePageWithId("1"))
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                       nonOwnerNonAdmin
                                )
                        )
        ).andDo(print());
    }
}
