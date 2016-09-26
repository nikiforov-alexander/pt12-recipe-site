package com.techdegree.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Owner {

    // fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "owner")
    private List<Recipe> ownedRecipes = new ArrayList<>();

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Recipe> getOwnedRecipes() {
        return ownedRecipes;
    }

    public void setOwnedRecipes(List<Recipe> ownedRecipes) {
        this.ownedRecipes = ownedRecipes;
    }

    // constructors

    public Owner() {

    }

    public Owner(User user) {
        this.user = user;
    }

    // other methods

    public void addOwnedRecipe(Recipe recipe) {
        ownedRecipes.add(recipe);
    }
}
