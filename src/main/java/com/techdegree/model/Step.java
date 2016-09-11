package com.techdegree.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

// extends BaseEntity, so that
// @Id and @Version are inherited
public class Step extends BaseEntity {

    // fields

    @NotNull
    @NotEmpty
    private String description;

    // getters and setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
