package com.techdegree.web.controller;

import com.techdegree.model.Ingredient;
import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
import com.techdegree.service.ItemService;
import com.techdegree.service.RecipeService;
import com.techdegree.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private ItemService itemsService;

    // validator is used this way because
    // recipe.ingredients.recipe is null upon saving
    // that is why we set ingredients to recipe, and
    // then proceed
    @Autowired
    private Validator validator;

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

    // detail recipe page
    @RequestMapping("/{id}")
    public String detailRecipePage(
            @PathVariable Long id,
            Model model) {
        Recipe recipe = recipeService.findOne(id);
        model.addAttribute("recipe", recipe);
        return "detail";
    }

    // edit recipe page GET
    // it does not work when "/id/edit" in tests :(
    @RequestMapping("/edit/{id}")
    public String editRecipePage(
            @PathVariable Long id,
            Model model) {

        Recipe recipe = recipeService.findOne(id);

        // if to this page we get from error post request, we
        // will not add recipe to model, because it will
        // be added with redirect attributes
        // Otherwise, recipe will be added from database
        if (!model.containsAttribute("recipe")) {
            model.addAttribute("recipe", recipe);
        }

        model.addAttribute("categories", RecipeCategory.values());

        // add items for ingredient.item field ... for now
        // items are in @OneToOne relationship. Later this can be changed
        // or improved. For now it is what it is
        model.addAttribute("items", itemsService.findAll());

        // check recipe
        // add "action" attribute, will be "/recipes/id/save"
        // in case of new will be "/recipes/add-new"
        model.addAttribute("action", "/recipes/"
                + recipe.getId() +
                "/save");

        return "edit";
    }

    // POST request to change saved item
    @RequestMapping(value = "/{id}/save", method = RequestMethod.POST)
    public String saveRecipe(
            Recipe recipe, // no @Valid here, it comes later
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @PathVariable Long id,
            Model model
    ) {
        // for each recipe.ingredient we set
        // recipe. Thymeleaf cannot make it right somehow ...
        // after that we can write if (result.hasErrors())
        recipe.getIngredients().forEach(
                i -> i.setRecipe(recipe)
        );
        validator.validate(recipe, bindingResult);

        // check validation for simple fields
        if (bindingResult.hasErrors()) {
            // set flash message with
            // errors, recipe, bindingResult with errors
            // and redirect
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.recipe",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute(
                    "recipe", recipe
            );
            redirectAttributes.addFlashAttribute(
                    "flash",
                    new FlashMessage(
                            "Oops! Some fields have errors",
                            FlashMessage.Status.FAILURE
                    )
            );
            // back to "edit" page
            return "redirect:/recipes/edit/" + recipe.getId();
        }
        return "redirect:/recipes/";
    }
}
