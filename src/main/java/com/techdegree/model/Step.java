package com.techdegree.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

// extends BaseEntity, so that
// @Id and @Version are inherited
@Entity
public class Step extends BaseEntity {

    // fields

    @NotNull
    @NotEmpty
    private String description;

    @NotNull(message =
        "Step cannot be created without recipe")
    @ManyToMany
    private List<Recipe> recipes = new ArrayList<>();

    // getters and setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    // constructors

    // default constructor for JPA with
    // parent constructor call
    public Step() {
        super();
    }

    // constructor to be used in DataLoader for
    // easier load of data and testing
    public Step(String description) {
        this();
        this.description = description;
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

}
