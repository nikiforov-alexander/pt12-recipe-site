package com.techdegree.web.controller;

import com.techdegree.model.Ingredient;
import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
import com.techdegree.model.Step;
import com.techdegree.service.IngredientService;
import com.techdegree.service.ItemService;
import com.techdegree.service.RecipeService;
import com.techdegree.service.StepService;
import com.techdegree.web.FlashMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

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
    }

    @Test
    public void updatingRecipeWithAllValidFieldsWorks()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real all, DatabaseLoader is used
        // to populate data
        int numberOfRecipesBeforePostRequest =
                recipeService.findAll().size();

        // When POST request for updating firstRecipeFromDatabase
        // with all correct parameters is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be RECIPES_HOME_PAGE
        // - flash message should be sent with success status
        mockMvc.perform(
        post(BASE_URI + "/recipes/save")
                .param("id",
                        firstRecipeFromDatabase.getId().toString())
                .param("version",
                        firstRecipeFromDatabase.getVersion().toString())
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
                .param("recipe.ingredients[0]",
                        testRecipeWithAllValidFields1.getIngredients().get(0)
                                .toString())
                .param("recipe.steps[0]",
                        testRecipeWithAllValidFields1.getSteps().get(0)
                                .toString())
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
        // Assert that number of recipes didn't change
        assertThat(
                recipeService.findAll().size(),
                is(numberOfRecipesBeforePostRequest)
        );
    }

    @Test
    public void savingNewRecipeWithAllValidFieldsWorks()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real all, DatabaseLoader is used
        // to populate data
        // Calculate number of recipes before request
        int numberOfRecipesBeforePostRequest =
                recipeService.findAll().size();

        // When POST request
        // or adding new one (because they are same)
        // with all
        // correct parameters is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be RECIPES_HOME_PAGE
        // - flash message should be sent with success status
        mockMvc.perform(
            post(BASE_URI + "/recipes/save")
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
                .param("recipe.ingredients[0]",
                        testRecipeWithAllValidFields1.getIngredients().get(0).toString())
                .param("recipe.steps[0]",
                        testRecipeWithAllValidFields1.getSteps().get(0).toString())
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
        // Assert that number of recipes increased
        assertThat(
                recipeService.findAll().size(),
                is(numberOfRecipesBeforePostRequest + 1)
        );
    }

    /**
     * private method used in test below to add test recipe
     * to database, the recipe that will be deleted afterwards
     * @return id of recipe to be deleted
     * @throws Exception because we use mockMvc and it throws
     * exception
     */
    private int addRecipeToBeDeletedAfterwards() throws Exception {
        mockMvc.perform(
        post(BASE_URI + "/recipes/save")
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
                .param("recipe.ingredients[0]",
                        testRecipeWithAllValidFields1.getIngredients().get(0).toString())
                .param("recipe.steps[0]",
                        testRecipeWithAllValidFields1.getSteps().get(0).toString())
        );
        return recipeService.findAll().size();
    }

    @Test
    public void deletingRecipeShouldBePossible() throws Exception {
        // Arrange:
        // mockMvc is set up as real and DatabaseLoader is used
        // to populate data

        // Calculate number of recipes, ingredients
        // and steps before request. To check consistency
        // afterwards
        int numberOfIngredientsBeforeAddingRecipeToDelete =
                ingredientService.findAll().size();
        int numberOfStepsBeforeAddingRecipeToDelete =
                stepService.findAll().size();
        int numberOfRecipesBeforeAddingRecipesToDelete =
                recipeService.findAll().size();

        // Add recipe to be deleted and get its id to
        // pass to request
        int idOfNewlyAddedRecipe = addRecipeToBeDeletedAfterwards();

        // When POST request to "/recipes/delete/idOfNewlyAddedRecipe"
        // is made
        // Then :
        // - status should be redirect 3xx
        // - redirected page should be RECIPES_HOME_PAGE
        // - successful flash should be sent
        mockMvc.perform(
                post("/recipes/delete/" + idOfNewlyAddedRecipe)
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
        // should be as it was, i.e. number of recipes
        // ingredients and steps added with this request
        // should stay the same
        assertThat(
                recipeService.findAll().size(),
                is(numberOfRecipesBeforeAddingRecipesToDelete)
        );
        assertThat(
                ingredientService.findAll().size(),
                is(numberOfIngredientsBeforeAddingRecipeToDelete)
        );
        assertThat(
                stepService.findAll().size(),
                is(numberOfStepsBeforeAddingRecipeToDelete)
        );
    }
}
