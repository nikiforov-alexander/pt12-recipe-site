package com.techdegree.model;

// This will be first iteration of RecipeCategory
// it will be enum and values will be pre-defined
// I think it is a good idea to extract RecipeCategory
// from here and be able to add new one
public enum RecipeCategory {
    BREAKFAST("Breakfast", "breakfast-category"),
    LUNCH("Lunch", "lunch-category"),
    DINNER("Dinner", "dinner-category"),
    DESSERT("Dessert", "dessert-category");

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
}
