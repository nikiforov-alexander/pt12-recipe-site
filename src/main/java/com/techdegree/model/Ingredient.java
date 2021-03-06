package com.techdegree.model;

import com.techdegree.validator.ValidItem;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

// extends Base entity, so @Version and @Id
// come from parent
@Entity
public class Ingredient extends BaseEntity {

    // fields

    // @ValidItem annotation is used to validate
    // whether the Item is valid. It is used
    // because when we select Item for ingredient
    // by default nothing is selected, and if
    // nothing is selected, Item.id will be 0
    // exactly this is checked here.
    @NotNull
    @OneToOne
    @JoinColumn(name = "item_id")
    @ValidItem(message = "Please select item")
    private Item item;

    @NotNull
    @NotEmpty(message = "Please input condition")
    private String condition;

    @NotNull
    @NotEmpty(message = "Please input quantity")
    private String quantity;

    // this field is needed so that user cannot
    // create Ingredient without Recipe. They are
    // bound together. The may be independent
    // but I leave them unique for each recipe
    @NotNull(message =
            "Ingredient cannot be created without recipe")
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    // getters and setters

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    // constructors

    // default constructor for JPA, using
    // super to include @Id and @Version fields
    public Ingredient() {
        super();
    }

    // constructor to be used in DataLoader to pre-load
    // data easier
    public Ingredient(Item item, String condition, String quantity) {
        this();
        this.item = item;
        this.condition = condition;
        this.quantity = quantity;
    }
}
