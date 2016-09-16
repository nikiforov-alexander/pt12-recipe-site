package com.techdegree.web.controller;

import com.techdegree.model.RecipeCategory;
import com.techdegree.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    // autowire others ??? for now no ...

    // home page with all recipes
    @RequestMapping("/")
    public String homePageWithAllRecipes(Model model) {
        model.addAttribute("recipes", recipeService.findAll());
        model.addAttribute("categoriesWithoutDefaultOne",
                RecipeCategory.valuesWithoutOne());
        model.addAttribute("defaultCategory", RecipeCategory.NONE);
        return "index";
    }

    // sorting recipes by .. pages

}
