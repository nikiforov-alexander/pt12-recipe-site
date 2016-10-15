package com.techdegree.handler;

import com.techdegree.dao.RecipeDao;
import com.techdegree.exception.CustomAccessDeniedException;
import com.techdegree.exception.UserAlreadyExistsException;
import com.techdegree.model.Recipe;
import com.techdegree.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Recipe.class)
public class RecipeEventHandler {
    @Autowired
    private RecipeDao recipeDao;

    @HandleBeforeCreate
    public void addOwnerBasedOnLoggedInUser(Recipe recipe) {
        User user = (User)
                SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        recipe.setOwner(user);
    }

    @HandleBeforeSave
    public void checkIfOwnerOrAdminIsEditing(Recipe recipe)
    throws UserAlreadyExistsException {
        User user = (User)
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();
        // if user is not owner or user is not admin
        // we throw access denied exception
        if (!recipe.getOwner().equals(user) &&
                !user.getRole().getName().equals("ROLE_ADMIN")
                ) {
            throw new CustomAccessDeniedException(
                    "Only user can edit Recipe"
            );
        }
    }

}
