package com.techdegree.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class RecipeCategoryTest {
    @Test
    public void listOfRecipeCategoriesWithoutDefaultOneIsProperlyGenerated()
            throws Exception {
        // Arrange: we test static methods, so no arrange

        assertThat(
                RecipeCategory.valuesWithoutOne(),
                iterableWithSize(
                        RecipeCategory.values().length - 1
                )
        );

        assertThat(
                RecipeCategory.valuesWithoutOne(),
                not(
                        hasItem(RecipeCategory.NONE)
                )
        );
    }

    @Test
    public void recipeCategoryCanBeFoundByHtmlName() throws Exception {
        assertThat(
                RecipeCategory.getRecipeCategoryWithHtmlName(
                        RecipeCategory.BREAKFAST.getHtmlName()
                ),
                is(RecipeCategory.BREAKFAST)
        );
    }
    @Test
    public void whenRecipeCategoryIsNotFoundByHtmlNameItShouldReturnNone()
            throws Exception {
        assertThat(
                RecipeCategory.getRecipeCategoryWithHtmlName(
                        "some name that is not there"
                ),
                is(RecipeCategory.NONE)
        );
    }
}