package com.techdegree.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// does not extend BaseEntity because
// i don't want version for it
@Entity
public class Role {

    // fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // constructors. Why make null ?
    // no answer ...

    public Role() {
        this.id = null;
        this.name = null;
    }

    public Role(String name) {
        this.id = null;
        this.name = name;
    }
}
