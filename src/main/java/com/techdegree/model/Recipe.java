package com.techdegree.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Recipe extends BaseEntity{

    // fields

    @NotNull
    @NotEmpty
    private String name;

    @NotEmpty
    @NotNull
    private String description;

    @NotNull
    @Enumerated
    private RecipeCategory recipeCategory;

    // for now we'll make it photo url, later
    // hopefully user can upload picture
    @NotEmpty
    @NotNull
    private String photoUrl;

    @NotNull
    @NotEmpty
    private String preparationTime;

    @NotNull
    @NotEmpty
    private String cookTime;

    @NotNull
    @OneToMany(mappedBy = "recipe")
    private List<Ingredient> ingredients = new ArrayList<>();

    @NotNull
    @ManyToMany
    private List<Step> steps = new ArrayList<>();

    // getters and setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RecipeCategory getRecipeCategory() {
        return recipeCategory;
    }

    public void setRecipeCategory(RecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    // constructors

    // default constructor for JPA, with parent
    // constructor inside
    public Recipe() {
        super();
    }

    // constructor to be used in DataLoader for easier
    // data injection and testing
    public Recipe(
            String name,
            String description,
            RecipeCategory recipeCategory,
            String photoUrl,
            String preparationTime,
            String cookTime) {
        this();
        this.name = name;
        this.description = description;
        this.recipeCategory = recipeCategory;
        this.photoUrl = photoUrl;
        this.preparationTime = preparationTime;
        this.cookTime = cookTime;
    }

    // additional methods adding Ingredients or Steps

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
    }

    public void addStep(Step step) {
        steps.add(step);
    }
}
