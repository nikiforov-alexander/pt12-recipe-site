package com.techdegree.web.controller;

import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
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

import static org.hamcrest.Matchers.*;
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

    private void createTestRecipeWithAllValidFields() {
        testRecipeWithAllValidFields1 = new Recipe.RecipeBuilder(1L)
                .withName("name 1")
                .withDescription("description 1")
                .withRecipeCategory(RecipeCategory.BREAKFAST)
                .withPhotoUrl("photo url 1")
                .withPreparationTime("prepTime 1")
                .withCookTime("cookTime 1")
                .build();
        testRecipeWithAllValidFields1.addIngredient(
                ingredientService.findOne(1L)
        );
        testRecipeWithAllValidFields1.addStep(
                stepService.findOne(1L)
        );
    }

    @Before
    public void setUp() throws Exception {
        BASE_URI = "http://localhost:" + actualPortNumber;
        mockMvc =
                webAppContextSetup(webApplicationContext)
                        .build();
        createTestRecipeWithAllValidFields();
    }

    // test members and methods used to generate test data:
    // both for Recipe and Item


    @Test
    public void updatingRecipeWithAllInvalidFieldsShouldGiveThatNumberOfErrors()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real all, DatabaseLoader is used
        // to populate data

        // When POST request changing existing Recipe with all
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be "/recipes/edit/1"
        // - flash message should be sent with failure status
        // - bindingResult should have 11 errors by the number of
        // input fields
        mockMvc.perform(
                post(BASE_URI + "/recipes/save/1")
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

        // When POST request changing existing Recipe with all
        // correct parameters is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be "/recipes/"
        // - flash message should be sent with success status
        mockMvc.perform(
            post(BASE_URI + "/recipes/save/1")
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
                redirectedUrl("/recipes/")
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
    }
}
