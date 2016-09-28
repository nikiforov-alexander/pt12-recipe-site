package com.techdegree.handler;

import com.techdegree.dao.OwnerDao;
import com.techdegree.dao.RecipeDao;
import com.techdegree.model.Owner;
import com.techdegree.model.Recipe;
import com.techdegree.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Recipe.class)
public class RecipeEventHandler {
    @Autowired
    private RecipeDao recipeDao;
    @Autowired
    private OwnerDao ownerDao;

    @HandleBeforeCreate
    public void addOwnerBasedOnLoggedInUser(Recipe recipe) {
        User user = (User)
                SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Owner owner = ownerDao.save(
                new Owner(user)
        );
        recipe.setOwner(owner);
    }
}