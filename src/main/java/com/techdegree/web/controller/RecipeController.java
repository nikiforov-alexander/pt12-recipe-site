package com.techdegree.web.controller;

import com.techdegree.model.*;
import com.techdegree.service.*;
import com.techdegree.web.FlashMessage;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.techdegree.web.WebConstants.RECIPES_HOME_PAGE;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private ItemService itemsService;
    @Autowired
    private StepService stepService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private CustomUserDetailsService userService;

    // validator is used this way because
    // recipe.ingredients.recipe is null upon saving
    // that is why we set ingredients to recipe, and
    // then proceed
    @Autowired
    private Validator validator;

    // home page with all recipes
    @RequestMapping("/")
    public String homePageWithAllRecipes(Model model) {
        List<Recipe> recipes = recipeService.findAll();
        model.addAttribute("recipes", recipes);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean[] recipesIsFavorites = new boolean[recipes.size()];

        User user = (User) authentication.getPrincipal();

        List<Recipe> favoriteRecipes = user.getFavoriteRecipes();

        for (int i = 0; i < recipes.size(); i++) {
            if (favoriteRecipes.contains(
                    recipes.get(i))
            ) {
                recipesIsFavorites[i] = true;
            } else {
                recipesIsFavorites[i] = false;
            }
        }

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

    /**
     *  Method generates Recipe with one ingredient
     *  with empty Item and other fields and empty
     *  Step, this way when it is passed to model,
     *  the page will be generated the way I wanted to,
     *  with one Ingredient and one step
     * @return Recipe with ingredient and step to be added
     * to model in "add-new" recipe page
     */
    private Recipe generateEmptyRecipeToPassToAddNewPage() {
        Recipe recipe = new Recipe();
        Item item = new Item();
        Ingredient ingredient = new Ingredient(item, "", "");
        recipe.addIngredient(ingredient);

        Step step = new Step("");
        recipe.addStep(step);
        return recipe;
    }

    /**
     * Because the same attributes are passed to Model when
     * generating add-new and edit recipe pages, I decided
     * to re-use that in a tricky way: I pass two args:
     * model and recipe, and in this method following attributes
     * are added:
     * "recipe" - if model does not contain it already
     * "categories" - values of RecipeCategories enum
     * "items" - possible Items from database
     * "action" - that is same for both "add-new" and "edit", action
     * is address where POST request will be made
     * @param recipe : recipe to be added to model
     * @param model : model to which attributes will be added
     * @return model that is filled with attrbutes
     */
    private Model addAttributesToModelForBothEditAndAddNewPages(
            Recipe recipe,
            Model model
    ) {
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
        model.addAttribute("action", "/recipes"
                + "/save");
        return model;
    }

    // add new recipe page GET
    @RequestMapping("/add-new")
    public String addNewRecipePage(Model model) {
        // empty recipe with ingredients and steps
        // is generated in separate method for
        // testing purposes
        model = addAttributesToModelForBothEditAndAddNewPages(
                generateEmptyRecipeToPassToAddNewPage(),
                model
        );

        return "edit";
    }

    // edit recipe page GET
    // it does not work when "/id/edit" in tests :(
    @RequestMapping("/edit/{id}")
    public String editRecipePage(
            @PathVariable Long id,
            Model model) {
        // here recipe found in database will be added
        model = addAttributesToModelForBothEditAndAddNewPages(
                recipeService.findOne(id),
                model
        );

        return "edit";
    }

    // POST request to change saved item
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveRecipe(
            Recipe recipe, // no @Valid here, it comes later
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // This is courage try to re-use method adding new
        // or saving edited item ... It may be wrong to do so
        // but I decided that methods look too similar
        // to make them separate ...
        // So if recipe passed has id, then we will be redirected
        // to "/edit/id" page, if not - to "/add-new"
        String pageFromWherePostReqWasMade = "/recipes/add-new";
        if (recipe.getId() != null) {
            pageFromWherePostReqWasMade = "/recipes/edit/" +
                    recipe.getId();
        }
        // for each recipe.ingredient and recipe.step we set
        // recipe. Thymeleaf cannot make it right somehow ...
        // after that we can write if (result.hasErrors())
        recipe.getIngredients().forEach(
                i -> i.setRecipe(recipe)
        );
        recipe.getSteps().forEach(
                s -> s.setRecipe(recipe)
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
            // back to "edit" or "add-new" page
            return "redirect:" + pageFromWherePostReqWasMade;
        }

        // for each recipe.ingredient.item we take id and
        // get item from database, because I don't know how
        // to pass in thymeleaf whole item object
        // at this point we are sure that item id will be set
        // because otherwise it will throw error in hasErrors()
        // check above
        recipe.getIngredients().forEach(
                i -> i.setItem(
                        itemsService.findOne(
                                i.getItem().getId()
                        )
                )
        );
        // if everything is OK, we save recipes, steps
        // and ingredients
        // because steps and ingredients
        // have foreign key in their table
        // so we have to do that
        recipeService.save(recipe);
        recipe.getIngredients().forEach(
                i -> ingredientService.save(i)
        );
        recipe.getSteps().forEach(
            step -> stepService.save(step)
        );

        // we also set good flash message with redirect
        // attributes, that recipes is successfully
        // updates
        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage(
                        "Recipe '" + recipe.getName() + "' was " +
                                "successfully saved!",
                        FlashMessage.Status.SUCCESS
                )
        );
        return "redirect:" + RECIPES_HOME_PAGE;
    }

    // delete recipe POST request
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deleteRecipe(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        Recipe recipe = recipeService.findOne(id);
        recipeService.delete(recipe);
        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage(
                        "Recipe '" + recipe.getName() +
                                "' was successfully deleted!",
                        FlashMessage.Status.SUCCESS
                )
        );
        return "redirect:" + RECIPES_HOME_PAGE;
    }
}
