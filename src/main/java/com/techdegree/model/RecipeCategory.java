package com.techdegree.model;

import java.util.Arrays;
import java.util.List;

// This will be first iteration of RecipeCategory
// it will be enum and values will be pre-defined
// I think it is a good idea to extract RecipeCategory
// from here and be able to add new one
public enum RecipeCategory {
    NONE("Other", "category other"),
    BREAKFAST("Breakfast", "category breakfast"),
    LUNCH("Lunch", "category lunch"),
    DINNER("Dinner", "category dinner"),
    DESSERT("Dessert", "category dessert");

    // fields

    private final String name;
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

    // constructors

    RecipeCategory(String name, String styleClass) {
        this.name = name;
        this.styleClass = styleClass;
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
}
