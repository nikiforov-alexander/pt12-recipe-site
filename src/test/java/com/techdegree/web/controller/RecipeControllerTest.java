package com.techdegree.web.controller;

import com.techdegree.model.Item;
import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
import com.techdegree.service.ItemService;
import com.techdegree.service.RecipeService;
import com.techdegree.web.FlashMessage;
// these matchers decided to come non-static ... I hope its ok
// for now don't know how to fix them
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsIterableContainingInOrder;
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

import static com.techdegree.web.WebConstants.RECIPES_HOME_PAGE;
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
    @Mock
    private ItemService itemService;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(
                recipeController
        ).build();
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

    private List<Item> createTwoTestItemsAndPutThemToList() {
        Item testItem1 = new Item("item 1");
        Item testItem2 = new Item("item 2");
        testItem1.setId(1L);
        testItem2.setId(2L);
        return Arrays.asList(
                testItem1, testItem2
        );
    }

    @Test
    public void favoritesWithNonNullsListIsGeneratedCorrectly()
            throws Exception {
        // Arrange:
        // create three new recipes
        Recipe r1 = new Recipe();
        Recipe r2 = new Recipe();
        Recipe r3 = new Recipe();
        // put all of them to "allRecipes" list
        // and two of them to "favoriteRecipes" list
        List<Recipe> allRecipes = Arrays.asList(
                r1,r2,r3
        );
        List<Recipe> favoriteRecipes = Arrays.asList(
                r2,r1
        );

        // Act: When generating favorites with nulls method is called
        List<Recipe> generatedFavoritesWithNullsList =
                recipeController
                        .generateFavoritesWithNullsForNonFavoritesList(
                            allRecipes, favoriteRecipes
                        );

        // Assert: Then generated list should be of size 3
        // and contain items in order r1,r2,null
        assertThat(
               generatedFavoritesWithNullsList,
               IsCollectionWithSize.hasSize(3)
        );
        assertThat(
                generatedFavoritesWithNullsList,
                IsIterableContainingInOrder.contains(
                       r1,r2,null
                )
        );

    }

    @Test
    public void home_returnsPageWithRecipes() throws Exception {
        // Arrange : mockMvc created with RecipeController in
        // mock recipeService to return couple of test recipes
        List<Recipe> recipes =
                createTwoTestRecipesAndPutThemToList();
        when(recipeService.findAll()).thenReturn(recipes);
        // arrange that when recipeService will be called to
        // return favorite recipes, we return all recipes
        when(recipeService.findFavoriteRecipesForUser(any()))
                .thenReturn(recipes);

        // Act and Assert:
        // When request to home page with RECIPES_HOME_PAGE
        // made,
        // Then:
        // - status should be OK
        // - view should be "index"
        // - model should have attributes "recipes"
        // - model should have attribute "favoritesWithNullsForNonFavorites"
        // NOTE: I'm not going to test Categories yet
        // because I'll may be push this functionality
        // to JS ...
        mockMvc.perform(
                get(BASE_URI + RECIPES_HOME_PAGE)
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(
                        model().attribute(
                                "recipes", recipes
                        )
                )
                .andExpect(
                        model().attribute(
                            "favoritesWithNullsForNonFavorites",
                            recipes
                        )
                );
        // Then recipe service.findAll() should be called
        verify(recipeService).findAll();
        verify(recipeService).findFavoriteRecipesForUser(any());
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

    @Test
    public void editPage_rendersCorrectly() throws Exception {
        // Arrange: mockMvc is set up with injected Controller
        // recipeService will return testRecipe1 when
        // findOne is called
        when(recipeService.findOne(1L)).thenReturn(
                testRecipe1
        );
        // Arrange itemService to return two test items
        // when findAll() is called
        List<Item> testItems = createTwoTestItemsAndPutThemToList();
        when(itemService.findAll()).thenReturn(
               testItems
        );

        // Act and Assert:
        // When GET request to recipe edit page is made
        // Then:
        // - status should be OK
        // - view should be "edit"
        // - model should contain "recipe", "items", "categories"
        // TODO: make a small class example, and show Craig and Chris
        // that this does not work when /recipes/1/edit :(
        mockMvc.perform(
                get(BASE_URI + "/recipes/edit/1")
        ).andDo(print())
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name("edit")
                )
                .andExpect(
                        model().attribute("recipe", testRecipe1)
                )
                .andExpect(
                        model().attribute("categories", RecipeCategory.values())
                )
                .andExpect(
                        model().attribute("items", testItems)
                );
        // Then both recipe and itemService should be called
        verify(recipeService).findOne(1L);
        verify(itemService).findAll();
    }

    @Test
    public void addNewRecipePageGetRequestGeneratesCorrectly() throws Exception {

        // Arrange: mockMvc is set up with injected Controller
        // Arrange itemService to return two test items
        // when findAll() is called
        List<Item> testItems = createTwoTestItemsAndPutThemToList();
        when(itemService.findAll()).thenReturn(
                testItems
        );

        // Act and Assert:
        // When GET request to recipe edit page is made
        // Then:
        // - status should be OK
        // - view should be "edit"
        // - model should contain :
        //  - "recipe",
        //  - "items",
        //  - "categories"
        //  - "action"
        mockMvc.perform(
                get(BASE_URI + "/recipes/add-new")
        ).andDo(print())
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name("edit")
                )
                .andExpect(
                        model().attribute("recipe",
                                Matchers.any(Recipe.class)
                        )
                )
                .andExpect(
                        model().attribute("categories", RecipeCategory.values())
                )
                .andExpect(
                        model().attribute(
                                "action",
                                "/recipes/save"
                        )
                )
                .andExpect(
                        model().attribute("items", testItems)
                );
        // Then itemService should be called
        verify(itemService).findAll();
    }

    // deleting

    @Test
    public void deletingRecipeShouldBePossible() throws Exception {
        // Arrange: mockMvc is arranged with Controller and mocks
        // make findOne return testRecipe when it is called
        when(recipeService.findOne(1L)).thenReturn(
                testRecipe1
        );
        // make delete do something
        doAnswer(
            invocation -> {
                Recipe r = invocation.getArgumentAt(0, Recipe.class);
                r.setId(1L);
                return r;
            }
        ).when(recipeService).delete(any(Recipe.class));

        // Act and Assert:
        // When delete request is made to "/recipes/delete/1"
        // Then:
        // - status should be redirect 3xx
        // - successful flash should be as attribute
        // - redirect page should be RECIPES_HOME_PAGE home page
        mockMvc.perform(
                post("/recipes/delete/1")
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
                        Matchers.hasProperty(
                                "status",
                                Matchers.equalTo(FlashMessage.Status.SUCCESS)
                        )
                )
        );
        // Assert that delete and findByOne methods were called
        verify(recipeService).delete(any(Recipe.class));
        verify(recipeService).findOne(1L);
    }
}