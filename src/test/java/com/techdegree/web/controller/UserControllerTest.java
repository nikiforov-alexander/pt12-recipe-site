package com.techdegree.web.controller;

import com.techdegree.model.Recipe;
import com.techdegree.model.User;
import com.techdegree.service.RecipeService;
import org.hamcrest.Matchers;
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

import static com.techdegree.web.WebConstants.PROFILE_PAGE;
import static com.techdegree.web.WebConstants.PROFILE_TEMPLATE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(
               userController
        ).build();
    }

    @Test
    public void profilePageRendersCorrectlyWithTwoModelAttributes()
            throws Exception {
        // Arrange: set up mockMvc with userController mocked

        // Arrange testFavoritesList to be returned by service
        List<Recipe> testFavoritesList =
                Arrays.asList(
                        new Recipe(),
                        new Recipe()
                );

        // Arrange: when recipe service will be called for
        // favorite recipes, we will return testFavoritesList
        when(recipeService.findFavoriteRecipesForUser(any()))
                .thenReturn(testFavoritesList);

        // Act and Assert
        // When GET request to PROFILE_PAGE is made
        // Then :
        // - status should be OK
        // - view should be names PROFILE_TEMPLATE
        // - model should have "user" attribute
        // - model should have "favoriteRecipes" attribute
        mockMvc.perform(
                get(PROFILE_PAGE + "/")
        ).andDo(print())
        .andExpect(
                status().isOk()
        )
        .andExpect(
                view().name(PROFILE_TEMPLATE)
        )
        .andExpect(
                model().attribute(
                        "favoriteRecipes",
                        testFavoritesList
                )
        )
        .andExpect(
                model().attribute(
                        "user",
                        Matchers.any(User.class)
                )
        );
        // cannot test name :(
        //.andExpect(
        //        model().attributeExists("name")
        //);

        verify(recipeService).findFavoriteRecipesForUser(
                any(User.class)
        );
    }
}