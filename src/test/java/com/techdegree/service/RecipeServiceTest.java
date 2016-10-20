package com.techdegree.service;

import com.techdegree.dao.RecipeDao;
import com.techdegree.model.*;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceTest {
    @Mock
    private RecipeDao recipeDao;
    @Mock
    private CustomUserDetailsService userService;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private RecipeService recipeService = new RecipeServiceImpl();


    @Test
    public void savingNewRecipeSetsOwner()
            throws Exception {
        // Arrange: create test Recipe to be saved
        // with id = null
        Recipe testRecipeWithOneIngredientAndOneItemId = new Recipe();
        // Arrange some user to be owner
        User testUserOwner = new User();

        // Arrange:
        // when recipeDao.save will be called
        // we save some recipe
        doAnswer(
                invocation -> {
                    Recipe r = invocation.getArgumentAt(0, Recipe.class);
                    return r;
                }
        ).when(recipeDao).save(any(Recipe.class));

        // Act:
        // when we call recipeService.save method
        Recipe savedRecipe = recipeService.save(
                testRecipeWithOneIngredientAndOneItemId,
                testUserOwner
        );

        assertThat(
                "owner was set to recipe",
                savedRecipe.getOwner(),
                is(testUserOwner)
        );
        // verify mock interactions
        verify(recipeDao).save(any(Recipe.class));
    }

    @Test
    public void savingNewRecipeSetsIngredientItems()
            throws Exception {
        // Arrange: create test Recipe to be saved
        // with one ingredient and one item with only id
        // so that itemService will be called
        Recipe testRecipeWithOneIngredientAndOneItemId = new Recipe();
        Item itemWithOnlyId = new Item();
        itemWithOnlyId.setId(1L);
        Ingredient ingredient = new Ingredient(
                itemWithOnlyId, "", ""
        );
        testRecipeWithOneIngredientAndOneItemId.addIngredient(ingredient);

        // Arrange:
        // create item to be returned from service
        Item itemWithNameAndId = new Item("name");
        itemWithNameAndId.setId(1L);

        // Arrange
        // make itemService return item when called
        when(itemService.findOne(1L)).thenReturn(
                itemWithNameAndId
        );
        // Arrange:
        // when recipeDao.save will be called
        // we save some recipe
        doAnswer(
                invocation -> {
                    Recipe r = invocation.getArgumentAt(0, Recipe.class);
                    return r;
                }
        ).when(recipeDao).save(any(Recipe.class));


        // Act:
        // when we call recipeService.save method
        Recipe savedRecipe = recipeService.save(
                testRecipeWithOneIngredientAndOneItemId,
                any(User.class)
        );

        assertThat(
                "items were set for ingredient of recipe",
                savedRecipe.getIngredients().get(0).getItem(),
                is(itemWithNameAndId)
        );
        // verify mock interactions
        verify(itemService).findOne(1L);
        verify(recipeDao).save(any(Recipe.class));
    }

    // updating tests

    @Test
    public void
    updatingRecipeDoesNotChangeOwner()
            throws Exception {
        // Arrange: create test Recipe to be saved
        // with one ingredient
        Recipe testRecipe =
                new Recipe();
        testRecipe.setId(1L);

        User testUser = new User();
        testUser.setId(1L);
        testRecipe.setOwner(
                testUser
        );

        // Arrange:
        // make recipeService findOne return test
        // recipe upon 2 calls
        when(recipeDao.findOne(1L)).thenReturn(
                testRecipe
        );
        when(recipeDao.findOne(1L)).thenReturn(
                testRecipe
        );
        // Arrange:
        // when recipeDao.save will be called
        // we save recipe that was passed
        doAnswer(
                invocation -> {
                    Recipe r = invocation.getArgumentAt(0, Recipe.class);
                    return r;
                }
        ).when(recipeDao).save(any(Recipe.class));

        // Act:
        // when we call recipeService.save method
        // with some user, not equal to testUser owner
        Recipe savedRecipe = recipeService.save(
                testRecipe,
                new User()
        );

        assertThat(
                "recipe.owner is still set to testOwner",
                savedRecipe.getOwner(),
                is(testUser)
        );

        // verify mock interactions
        verify(recipeDao, times(2)).findOne(1L);
        verify(recipeDao).save(any(Recipe.class));
    }

    @Test
    public void
    updatingRecipeSetsRecipeItems()
            throws Exception {
        // Arrange: create test Recipe to be saved
        // with one ingredient and one item
        Recipe testRecipe =
                new Recipe();
        testRecipe.setId(1L);

        Item itemWithOnlyId = new Item();
        itemWithOnlyId.setId(1L);
        Ingredient ingredient = new Ingredient(
                itemWithOnlyId, "", ""
        );
        testRecipe.addIngredient(ingredient);

        // Arrange item to be returned
        // from database
        Item itemFromDatabase = new Item();
        itemFromDatabase.setId(1L);
        itemFromDatabase.setName("name");

        // Arrange:
        // make itemService return item when called
        when(itemService.findOne(1L)).thenReturn(
                itemFromDatabase
        );
        when(recipeDao.findOne(any())).thenReturn(
                testRecipe
        );
        when(recipeDao.findOne(any())).thenReturn(
                testRecipe
        );
        // Arrange:
        // when recipeDao.save will be called
        // we save recipe that was passed
        doAnswer(
                invocation -> {
                    Recipe r = invocation.getArgumentAt(0, Recipe.class);
                    return r;
                }
        ).when(recipeDao).save(any(Recipe.class));


        // Act: update recipe
        Recipe savedRecipe =
                recipeService.save(testRecipe, new User());

        assertThat(
                "item returned will item from database",
                savedRecipe.getIngredients().get(0).getItem(),
                is(itemFromDatabase)
        );

        // verify mock interactions
        verify(itemService).findOne(any());
        verify(recipeDao, times(2)).findOne(1L);
        verify(recipeDao).save(any(Recipe.class));
    }

    @Test
    public void
    updatingRecipeSavesFavorites()
            throws Exception {
        // Arrange: create test Recipe
        Recipe testRecipe =
                new Recipe();
        testRecipe.setId(1L);
        // Arrange test User that favored Recipe
        User testUser = new User();
        // Arrange set recipe favorites
        testRecipe.setFavoriteUsers(
                Collections.singletonList(
                        testUser
                )
        );

        // Arrange mocks:
        // make recipeService findOne return test
        // recipe upon 2 calls
        when(recipeDao.findOne(1L)).thenReturn(
                testRecipe
        );
        when(recipeDao.findOne(1L)).thenReturn(
                testRecipe
        );
        // Arrange mocks: when recipeDao.save will be called
        // we save recipe that was passed
        doAnswer(
                invocation -> {
                    Recipe r = invocation.getArgumentAt(0, Recipe.class);
                    return r;
                }
        ).when(recipeDao).save(any(Recipe.class));

        // Act:
        // when we call recipeService.save method
        // with some user,
        Recipe savedRecipe = recipeService.save(
                testRecipe,
                any(User.class)
        );

        assertThat(
                "recipe.favorites has size 1",
                savedRecipe.getFavoriteUsers(),
                iterableWithSize(1)
        );
        assertThat(
                "recipe.favorites contains testUser",
                savedRecipe.getFavoriteUsers(),
                containsInAnyOrder(testUser)
        );

        // verify mock interactions
        verify(recipeDao, times(2)).findOne(1L);
        verify(recipeDao).save(any(Recipe.class));
    }

    @Test
    public void updatingRecipeWhenRecipeIsAlreadyFavoriteShouldRemoveRecipeFromFavorites()
            throws Exception {
        // Arrange: Given Recipe with id = 1L
        // that will be added to list of favorite recipes
        Recipe recipeWithIdOne = new Recipe();
        recipeWithIdOne.setId(1L);

        // Arrange: add recipe to favorites
        List<Recipe> listOfFavoriteRecipesIds =
                Arrays.asList(recipeWithIdOne);

        // Arrange : Given that findFavoriteRecipesForUser
        // return listOfFavoriteRecipeIds above
        when(recipeService.findFavoriteRecipesForUser(
                Mockito.any(User.class))
        ).thenReturn(listOfFavoriteRecipesIds);

        // Arrange: Given that when called
        // recipeDao.removeFavoriteRecipeForUser will
        // return something
        doAnswer(
                invocation -> null
        ).when(recipeDao).removeFavoriteRecipeForUser(
                Mockito.anyLong(), Mockito.anyLong()
        );

        // Act and Assert:
        // When update favorites will be called with
        // first recipe and some user
        assertFalse(
                "false should be returned as indication that" +
                        "recipe is now NOT favorite",
                recipeService.updateFavoriteRecipesForUser(
                        recipeWithIdOne, new User()
                )
        );

        // Assert: Then recipeDao.addFavoriteRecipeForUser
        // should be called
        verify(recipeDao).removeFavoriteRecipeForUser(
                Mockito.anyLong(), Mockito.anyLong()
        );
    }

    @Test
    public void updatingRecipeWhenRecipeIsNotFavoriteShouldAddRecipeToFavorites()
            throws Exception {

        // Arrange : Given that findFavoriteRecipesForUser
        // return empty new List<Recipe>
        when(recipeService.findFavoriteRecipesForUser(
                Mockito.any(User.class))
        ).thenReturn(new ArrayList<>());

        // Arrange: Given that when called
        // recipeDao.addFavoriteRecipeForUser will
        // return something
        doAnswer(
                invocation -> null
        ).when(recipeDao).addFavoriteRecipeForUser(
                Mockito.anyLong(), Mockito.anyLong()
        );

        // Act and Assert:
        // When update favorites will be called with
        // some recipe and some user
        assertTrue(
                "true should be returned as indication that" +
                        "recipe is now NOT favorite",
                recipeService.updateFavoriteRecipesForUser(
                        new Recipe(), new User()
                )
        );

        // Assert: Then recipeDao.addFavoriteRecipeForUser
        // should be called
        verify(recipeDao).addFavoriteRecipeForUser(
                Mockito.anyLong(), Mockito.anyLong()
        );
    }
}