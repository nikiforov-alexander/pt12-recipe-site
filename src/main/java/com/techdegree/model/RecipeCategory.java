package com.techdegree.model;

import java.util.*;

// This will be first iteration of RecipeCategory
// it will be enum and values will be pre-defined
// I think it is a good idea to extract RecipeCategory
// from here and be able to add new one
public enum RecipeCategory {
    NONE("Other", "category other", "other"),
    BREAKFAST("Breakfast", "category breakfast", "breakfast"),
    LUNCH("Lunch", "category lunch", "lunch"),
    DINNER("Dinner", "category dinner", "dinner"),
    DESSERT("Dessert", "category dessert", "dessert");

    // fields

    private final String name;

    // this name will be used in query to sort
    // by category on index page
    private final String htmlName;
    // this field will be the name of respectful CSS
    // style class, that will let us change style
    // according to selected option
    private final String styleClass;

    // getters


    public String getName() {
        return name;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public String getHtmlName() {
        return htmlName;
    }

    // constructors

    RecipeCategory(
            String name,
            String styleClass,
            String htmlName) {
        this.name = name;
        this.styleClass = styleClass;
        this.htmlName = htmlName;
    }

    // TODO: fix this ugly code: for now it throws UnsupportedOpException
    public static List<RecipeCategory> valuesWithoutOne() {
       List<RecipeCategory> categoriesWithoutNone =
               Arrays.asList(
                    RecipeCategory.BREAKFAST,
                    RecipeCategory.DESSERT,
                    RecipeCategory.LUNCH,
                    RecipeCategory.DINNER
               );
       return categoriesWithoutNone;
    }

    public static RecipeCategory
    getRecipeCategoryWithHtmlName(String htmlName) {
        Map<String, RecipeCategory> mapWithHtmlNames =
                new HashMap<>();
        for (RecipeCategory recipeCategory : values()) {
            mapWithHtmlNames.put(
                    recipeCategory.getHtmlName(),
                    recipeCategory
            );
        }
        return mapWithHtmlNames.getOrDefault(
                        htmlName,
                        RecipeCategory.NONE
        );
    }
}
