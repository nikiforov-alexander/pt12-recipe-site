package com.techdegree.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    // getters and setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
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

}
