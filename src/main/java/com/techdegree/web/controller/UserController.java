package com.techdegree.web.controller;

import com.techdegree.model.User;
import com.techdegree.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.techdegree.web.WebConstants.PROFILE_PAGE;
import static com.techdegree.web.WebConstants.PROFILE_TEMPLATE;

@Controller
public class UserController {

    @Autowired
    private RecipeService recipeService;

    // for now I will not care about the case
    // whether unregistered user can see this
    // page ...
    @RequestMapping(PROFILE_PAGE)
    public String userProfilePage(
            Model model,
            @AuthenticationPrincipal User user
            ) {
        model.addAttribute(
                "name",
                user.getName()
        );
        model.addAttribute("favoriteRecipes",
                recipeService.findFavoriteRecipesForUser(
                        user
                )
        );
        return PROFILE_TEMPLATE;
    }
}
