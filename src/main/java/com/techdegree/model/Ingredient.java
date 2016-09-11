package com.techdegree.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

// extends Base entity, so @Version and @Id
// come from parent
@Entity
public class Ingredient extends BaseEntity {

    // fields

    @NotNull
    @NotEmpty
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull
    @NotEmpty
    private String condition;

    @NotNull
    @NotEmpty
    private String quantity;

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
