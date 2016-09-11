package com.techdegree.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

// extends BaseEntity, so @Id and @Version
// come from parent
// It is a separate class, because probably it is
// good idea to share items across recipes, or to
// make them specific to one user, may be?
@Entity
public class Item extends BaseEntity {

    // fields

    // for now Item will have uni-directional
    // relationship with Ingredient, and only
    // on ingredient side

    @NotNull
    @NotEmpty
    private String name;

    // getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // constructors

    // default constructor for JPA
    // calls parent constructor
    public Item() {
        super();
    }

    // constructor for DataLoader
    // calls default no-args constructor
    public Item(String name) {
        this();
        this.name = name;
    }
}
