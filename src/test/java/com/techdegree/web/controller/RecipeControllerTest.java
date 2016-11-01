package com.techdegree.web.controller;

import com.techdegree.model.*;
import com.techdegree.service.ItemService;
import com.techdegree.service.RecipeService;
import com.techdegree.web.FlashMessage;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.techdegree.web.WebConstants.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
        // - model should have attribute "categories" with
        //   all categories
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
        )
        .andExpect(
            model().attribute(
                    "categories", RecipeCategory.values()
            )
        );
        // Then recipe service.findAll() should be called
        verify(recipeService).findAll();
        verify(recipeService).findFavoriteRecipesForUser(any());
    }

    @Test
    public void detailRecipePageWithFavoriteRecipeShouldRenderSuccessfully()
            throws Exception {
        // Arrange : Given mockMvc arranged with injected Controller

        // Arrange: Given that findOne will return testRecipe1
        when(recipeService.findOne(1L)).thenReturn(
                testRecipe1
        );

        // Arrange : Given that checkIfRecipeIsFavoriteForUser will
        // return true
        when(
                recipeService.checkIfRecipeIsFavoriteForUser(
                        any(Recipe.class), any(User.class)
                )
        ).thenReturn(true);

        // Act and Assert
        // When request to detail page is made
        // Then:
        // - status should be OK
        // - view should be "detail"
        // - model should contain "recipe" attribute
        // - model attribute "favoriteImageSrc" should
        //   contain "favorited." indicating that recipe is
        //   favorite for user
        // - model attribute "favoriteButtonText" should
        //   contain "Remove" indication that by pressing
        //   we'll remove Recipe
        mockMvc.perform(
                get(BASE_URI + "/recipes/1")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(
                        model().attribute(
                                "favoriteImageSrc",
                                containsString("favorited.")
                        )
                )
                .andExpect(
                        model().attribute(
                                "favoriteButtonText",
                                containsString("Remove")
                        )
                )
                .andExpect(
                        model().attribute(
                                "recipe",
                                testRecipe1
                        )
                );

        // Verify mocks
        verify(recipeService).findOne(1L);
        verify(recipeService).checkIfRecipeIsFavoriteForUser(
                any(Recipe.class), any(User.class)
        );
    }

    @Test
    public void detailRecipePageWithNonFavoriteRecipeShouldRenderSuccessfully()
            throws Exception {
        // Arrange : Given mockMvc arranged with injected Controller

        // Arrange: Given that findOne will return testRecipe1
        when(recipeService.findOne(1L)).thenReturn(
                testRecipe1
        );

        // Arrange : Given that checkIfRecipeIsFavoriteForUser will
        // return false
        when(
                recipeService.checkIfRecipeIsFavoriteForUser(
                        any(Recipe.class), any(User.class)
                )
        ).thenReturn(false);

        // Act and Assert
        // When request to detail page is made
        // Then:
        // - status should be OK
        // - view should be "detail"
        // - model should contain "recipe" attribute
        // - model should contain "favoriteImageSrc"
        //   with "favorite." in it, rendering empty heart
        // - model should contain "Add" in "favoriteButtonText"
        //   attribute, indicating that by pressing we'll
        //   make Recipe favorite

        mockMvc.perform(
                get(BASE_URI + "/recipes/1")
        ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("detail"))
                .andExpect(
                        model().attribute(
                                "favoriteImageSrc",
                                containsString("favorite.")
                        )
                )
                .andExpect(
                        model().attribute(
                                "favoriteButtonText",
                                containsString("Add")
                        )
                )
                .andExpect(
                        model().attribute(
                                "recipe",
                                testRecipe1
                        )
                );

        // Verify mocks
        verify(recipeService).findOne(1L);
        verify(recipeService).checkIfRecipeIsFavoriteForUser(
                any(Recipe.class), any(User.class)
        );
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
                get(ADD_NEW_PAGE)
        ).andDo(print())
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name("edit")
                )
                .andExpect(
                        model().attributeExists("recipe")
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
                        hasProperty(
                                "status",
                                equalTo(FlashMessage.Status.SUCCESS)
                        )
                )
        );
        // Assert that delete and findByOne methods were called
        verify(recipeService).delete(any(Recipe.class));
        verify(recipeService).findOne(1L);
    }

    @Test
    public void recipesCanBeListedByCategoryOnIndexPage()
            throws Exception {
        // Arrange: mockMvc is arranged

        // Arrange testListWithTwoRecipes
        // that will be added to model
        List<Recipe> testListWithTwoRecipes =
                Arrays.asList(
                        new Recipe(),
                        testRecipe1
                );
        // Arrange: when find by recipe category name
        // will be called on service layer we return
        // test list with two recipes
        when(recipeService.findByRecipeCategoryName(
                anyString())).thenReturn(
                        testListWithTwoRecipes
        );

        // Arrange: set GET parameter
        String categoryNameParameter = "other";

        // Act:
        // When request with parameter is made to home
        // page with category=other parameter
        // Then :
        // - status should be OK
        // - model should contain "recipes" with our
        //   test list returned by service
        // - model should contain "categories" with
        //   all recipe categories
        // - model should contain "selectedCategory"
        //   with category found by parameter name
        mockMvc.perform(
                get("/recipes/?category=" + categoryNameParameter)
        ).andDo(print())
        .andExpect(
                status().isOk()
        )
        .andExpect(
                model().attribute(
                        "recipes",
                        testListWithTwoRecipes
                )
        )
        .andExpect(
                model().attribute(
                        "categories",
                        RecipeCategory.values()
                )
        )
        .andExpect(
                model().attribute("selectedCategory",
                        RecipeCategory.getRecipeCategoryWithHtmlName(
                                categoryNameParameter
                        ))
        );

        // verify mock interactions
        // Assert that findByRecipeCategoryName was
        // called with parameter that we passed
        verify(recipeService).findByRecipeCategoryName(
                categoryNameParameter
        );
    }

    @Test
    public void userCanAddRecipeToFavoritesFromDetailPage()
            throws Exception {
        // Given mocked Controller

        // Given that first recipe will be returned
        // when recipe service find one is called
        when(
                recipeService.findOne(any(Long.class))
        ).thenReturn(testRecipe1);

        // Given that "true" will be returned when
        // updateFavoriteRecipesForUser will be called
        when(
                recipeService.updateFavoriteRecipesForUser(
                        any(Recipe.class), any(User.class)
                )
        ).thenReturn(true);

        // When POST request to updateFavoriteStatusPageWithId(1)
        // is called
        // Then :
        // - status should be 3xx
        // - redirected page should be RECIPES_HOME_PAGE
        // - model attribute "flash" should have "added" String
        mockMvc.perform(
                post(updateFavoriteStatusPageWithId("1"))
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
                                        "message",
                                        containsString("added")
                                )
                        )
                );

        // Verify mocks
        verify(recipeService).findOne(any(Long.class));
        verify(recipeService).updateFavoriteRecipesForUser(
                any(Recipe.class), any(User.class)
        );
    }

    @Test
    public void userCanRemoveRecipeFromFavoritesFromDetailPage() throws Exception {
        // Given mocked Controller

        // Given that first recipe will be returned
        // when recipe service find one is called
        when(
                recipeService.findOne(any(Long.class))
        ).thenReturn(testRecipe1);

        // Given that "false" will be returned when
        // updateFavoriteRecipesForUser will be called
        when(
                recipeService.updateFavoriteRecipesForUser(
                        any(Recipe.class), any(User.class)
                )
        ).thenReturn(false);

        // When POST request to updateFavoriteStatusPageWithId(1)
        // is called
        // Then :
        // - status should be 3xx
        // - redirected page should be RECIPES_HOME_PAGE
        // - model attribute "flash" should have "removed" String
        mockMvc.perform(
                post(updateFavoriteStatusPageWithId("1"))
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
                                        "message",
                                        containsString("removed")
                                )
                        )
                );

        // Verify mocks
        verify(recipeService).findOne(any(Long.class));
        verify(recipeService).updateFavoriteRecipesForUser(
                any(Recipe.class), any(User.class)
        );
    }

    @Test(expected = NestedServletException.class)
    public void nonOwnerNonAdminCannotUpdateRecipe() throws Exception {

        // Given that when recipeService.checkForUser
        // will throw AccessDeniedException
        doThrow(
                AccessDeniedException.class
        ).when(recipeService)
                .checkIfUserCanEditRecipe(
                        any(User.class), any(Recipe.class)
                );

        // When we make POST request to update recipe
        // with "id = 1"
        mockMvc.perform(
                post("/recipes/save")
                .param("id", "1")
        ).andDo(print());

        // Then NestedServletException should be thrown
    }

    @Test(expected = NestedServletException.class)
    public void nonOwnerNonAdminCannotAccessEditRecipePage()
            throws Exception {
        // Given that AccessDeniedException will be thrown
        // when checkIfUserCanEditRecipe method on service
        // will be called
        doThrow(
                AccessDeniedException.class
        ).when(recipeService)
                .checkIfUserCanEditRecipe(
                        any(User.class), any(Recipe.class)
                );

        // When we make GET request to render "edit" page
        mockMvc.perform(
                get(getEditRecipePageWithId("1"))
        ).andDo(print());

        // Then NestedServletException should be thrown
    }

    @Test
    public void recipesCanBeSearchedByDescriptionOnIndexPage()
            throws Exception {
        // Given listWithOneTestRecipe with testRecipe1
        List<Recipe> listWithOneTestRecipe =
                Collections.singletonList(
                        testRecipe1
                );

        // Given that recipeService.findByRecipeDescription
        // will return listWithOneTestRecipe
        when(
                recipeService.findByDescriptionContaining(anyString())
        ).thenReturn(listWithOneTestRecipe);

        // Given that recipeService.findFavoriteRecipesForUser
        // will return new ArrayList<>
        when(
                recipeService.findFavoriteRecipesForUser(any(User.class))
        ).thenReturn(new ArrayList<>());

        // When GET request to RECIPES_HOME_PAGE is
        // made with "description" parameter.
        // Then:
        // - status should be OK
        // - view named INDEX_TEMPLATE
        // - model should contain "recipes" with
        //   our listWithOneTestRecipe
        mockMvc.perform(
                get(RECIPES_HOME_PAGE)
                .param("description", "1")
        ).andDo(print())
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(INDEX_TEMPLATE)
                )
                .andExpect(
                        model().attribute(
                                "recipes",
                                listWithOneTestRecipe
                        )
                );

        // verify mock interactions
        verify(recipeService).findFavoriteRecipesForUser(
                any(User.class)
        );
        verify(recipeService).findByDescriptionContaining(
                anyString()
        );
    }
}