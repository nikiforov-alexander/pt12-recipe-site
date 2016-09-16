package com.techdegree.web.controller;

import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
import com.techdegree.service.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class RecipeControllerTest {

    // constants

    private static final String BASE_URI =
            "http://localhost:8080";

    // mocked classes and dependencies

    private MockMvc mockMvc;

    @InjectMocks
    private RecipeController recipeController;

    @Mock
    private RecipeService recipeService;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(
                recipeController
        ).build();
    }

    // test members and methods used to generate test data:

    private Recipe testRecipe1 = new Recipe.RecipeBuilder(1L)
                .withName("name 1")
                .withDescription("description 1")
                .withRecipeCategory(RecipeCategory.BREAKFAST)
                .withPhotoUrl("photo url 1")
                .withPreparationTime("prepTime 1")
                .withCookTime("cookTime 1")
                .build();

    /**
     * It may be stupid to remove this code, but
     * I feel it is too cumbersome and not important
     * in some tests
     * @return List<Recipe> with two test recipes
     */
    private List<Recipe> createTwoTestRecipesAndPutThemToList() {
        Recipe testRecipe2 = new Recipe.RecipeBuilder(2L)
                .withName("name 2")
                .withDescription("description 2")
                .withRecipeCategory(RecipeCategory.BREAKFAST)
                .withPhotoUrl("photo url 2")
                .withPreparationTime("prepTime 2")
                .withCookTime("cookTime 2")
                .build();
        return Arrays.asList(
                testRecipe1, testRecipe2
        );
    }

    @Test
    public void home_returnsPageWithRecipes() throws Exception {
        // Arrange : mockMvc created with RecipeController in
        // mock recipeService to return couple of test recipes
        List<Recipe> recipes =
                createTwoTestRecipesAndPutThemToList();
        when(recipeService.findAll()).thenReturn(recipes);

        // Act and Assert:
        // When request to home page with "/recipes/"
        // made,
        // Then:
        // - status should be OK
        // - view should be "index"
        // - model should have attributes "recipes"
        // NOTE: I'm not going to test Categories yet
        // because I'll may be push this functionality
        // to JS ...
        mockMvc.perform(
                get(BASE_URI + "/recipes/")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(
                        model().attribute(
                                "recipes",
                                recipes
                        )
                );
        // Then recipe service.findAll() should be called
        verify(recipeService).findAll();
    }

    @Test
    public void detailRecipePage_shouldRenderSuccessfully()
            throws Exception {
        // Arrange : mockMvc is arranged with injected Controller
        // we arrange recipeService to return first test Recipe
        // when service.findOne(1L) will be called
        when(recipeService.findOne(1L)).thenReturn(
                testRecipe1
        );

        // Act and Assert
        // When request to detail page is made
        // Then:
        // - status should be OK
        // - view should be "detail"
        // - model should contain "recipe" attribute
        mockMvc.perform(
                get(BASE_URI + "/recipes/1")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(
                        model().attribute(
                                "recipe",
                                testRecipe1
                        )
                );
        // Then recipe service.findOne(1L) should be called
        verify(recipeService).findOne(1L);
    }
}