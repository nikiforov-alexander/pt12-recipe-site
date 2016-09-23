package com.techdegree.web.controller;

import com.techdegree.model.Item;
import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
import com.techdegree.service.ItemService;
import com.techdegree.service.RecipeService;
import com.techdegree.web.FlashMessage;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
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
    private WebApplicationContext webApplicationContext;


    @Before
    public void setUp() throws Exception {
        BASE_URI = "http://localhost:" + actualPortNumber;
        mockMvc =
                webAppContextSetup(webApplicationContext)
                        .build();
    }

    // test members and methods used to generate test data:
    // both for Recipe and Item

    private Recipe testRecipe1 = new Recipe.RecipeBuilder(1L)
            .withName("name 1")
            .withDescription("description 1")
            .withRecipeCategory(RecipeCategory.BREAKFAST)
            .withPhotoUrl("photo url 1")
            .withPreparationTime("prepTime 1")
            .withCookTime("cookTime 1")
            .build();

    @Test
    public void updatingValidRecipeWithoutIngredientsAndStepsWorks()
            throws Exception {
        // Arrange:
        // mockMvc is set up as real all, DatabaseLoader is used
        // to populate data

        // When POST request changing existing Recipe with all
        // correct parameters except 'Recipe.ingredients' and
        // 'Recipe.steps' is made
        // Then:
        // - status should be 3xx : redirect
        // - redirected page should be "/recipes/"
        // - flash message should be sent with success status
        mockMvc.perform(
            post(BASE_URI + "/recipes/save/1")
                .param("name", testRecipe1.getName())
                .param("description", testRecipe1.getDescription())
                .param("recipeCategory", testRecipe1.getRecipeCategory().toString())
                .param("photoUrl", testRecipe1.getPhotoUrl())
                .param("cookTime", testRecipe1.getCookTime())
                .param("preparationTime", testRecipe1.getPreparationTime())
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
                                "message",
                                containsString("was successfully updated")
                        )
                )
        );
    }
}
