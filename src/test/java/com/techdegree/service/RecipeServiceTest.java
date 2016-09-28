package com.techdegree.service;

import com.techdegree.dao.RecipeDao;
import com.techdegree.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RecipeServiceTest {
    @Mock
    private RecipeDao recipeDao;
    @Mock
    private OwnerService ownerService;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private RecipeService recipeService = new RecipeServiceImpl();


    @Test
    public void savingNewRecipeInvokesOwnerAndItemServicesAndSetsRecipeItems() throws Exception {
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
        // when owner.save will be called, we
        // save some owner
        doAnswer(
                invocation -> {
                    Owner owner = invocation.getArgumentAt(0, Owner.class);
                    return owner;
                }
        ).when(ownerService).save(any(Owner.class));
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
                new User()
        );

        // Assert that: items were set for ingredient of
        // recipe
        assertThat(
                savedRecipe.getIngredients().get(0).getItem(),
                is(itemWithNameAndId)
        );

        // verify mock interactions
        verify(itemService).findOne(1L);
        verify(ownerService).save(any(Owner.class));
        verify(recipeDao).save(any(Recipe.class));
    }
}