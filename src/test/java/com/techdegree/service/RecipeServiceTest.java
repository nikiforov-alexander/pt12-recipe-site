package com.techdegree.service;

import com.techdegree.dao.RecipeDao;
import com.techdegree.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
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
    public void savingNewRecipeSetsOwnerAndItemServicesAndSetsRecipeItems()
            throws Exception {
        // Arrange: create test Recipe to be saved
        // with one ingredient and one item with only id
        Recipe testRecipeWithOneIngredientAndOneItemId = new Recipe();
        Item itemWithOnlyId = new Item();
        itemWithOnlyId.setId(1L);
        Ingredient ingredient = new Ingredient(
                itemWithOnlyId, "", ""
        );
        testRecipeWithOneIngredientAndOneItemId.addIngredient(ingredient);

        // Arrange:
        // make itemService return item when called
        Item itemWithNameAndId = new Item("name");
        itemWithNameAndId.setId(1L);

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

        // Arrange some user to be owner
        User testUserOwner = new User();

        // Act:
        // when we call recipeService.save method
        Recipe savedRecipe = recipeService.save(
                testRecipeWithOneIngredientAndOneItemId,
                testUserOwner
        );

        assertThat(
                "items were set for ingredient of recipe",
                savedRecipe.getIngredients().get(0).getItem(),
                is(itemWithNameAndId)
        );
        assertThat(
                "owner was set to recipe",
                savedRecipe.getOwner(),
                is(testUserOwner)
        );
        // verify mock interactions
        verify(itemService).findOne(1L);
        verify(recipeDao).save(any(Recipe.class));
    }

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
    updatingRecipeDoesNotChangeOwnerAndSetsRecipeItems()
            throws Exception {
        // Arrange: create test Recipe to be saved
        // with one ingredient
        Recipe testRecipe =
                new Recipe();
        testRecipe.setId(1L);
        Ingredient ingredient = new Ingredient(
                new Item(), "", ""
        );
        testRecipe.addIngredient(ingredient);

        User testUser = new User();
        // Arrange set recipe owner and favorites
        testRecipe.setOwner(
                testUser
        );
        testRecipe.setFavoriteUsers(
                Collections.singletonList(
                        testUser
                )
        );

        // Arrange:
        // make itemService return item when called
        when(itemService.findOne(1L)).thenReturn(
                new Item()
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
        verify(itemService).findOne(any());
        verify(recipeDao, times(2)).findOne(1L);
        verify(recipeDao).save(any(Recipe.class));
    }
}