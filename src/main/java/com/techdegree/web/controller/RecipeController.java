package com.techdegree.web.controller;

import com.techdegree.model.*;
import com.techdegree.service.*;
import com.techdegree.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static com.techdegree.web.WebConstants.*;

@Controller
@ComponentScan
public class RecipeController {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private ItemService itemsService;
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

    /**
     * Generates List<Recipe> with the same size as "allRecipes"
     * but with nulls for non-favorite recipes
     * based on favoriteRecipes array
     * @param allRecipes - List<Recipe> with all recipes
     * @param favoriteRecipes - List<Recipe> with all favorite recipes
     * @return List<Recipe> with nulls on indices where, recipes are
     * not favorite. Check
     * RecipeControllerTest.favoritesWithNonNullsListIsGeneratedCorrectly()
     * for more
     */
    List<Recipe> generateFavoritesWithNullsForNonFavoritesList(
            List<Recipe> allRecipes,
            List<Recipe> favoriteRecipes
    ) {
        List<Recipe> favoriteRecipesWithNullsForNonFavorites =
                new ArrayList<>();
        allRecipes.forEach(
                r -> {
                    if (favoriteRecipes.contains(r)) {
                        favoriteRecipesWithNullsForNonFavorites.add(r);
                    } else {
                        favoriteRecipesWithNullsForNonFavorites.add(null);
                    }
                }
        );
        return favoriteRecipesWithNullsForNonFavorites;
    }

    /**
     * this method is extracted to re-use same addition of
     * model attributes for both "/" and "/?category"
     * request mappings. It takes a model, and returns the
     * same model but with attributes. Whether this
     * strategy is good, or not, I don't know, but
     * it definitely makes code DRY
     * @param model Spring UI Model
     * @param recipes <code>List<Recipe></code> that can be all recipes
     *                can be only recipes with specified category
     * @param user com.techdegree.model.User,
     *             and @AuthenticationPrincipal, he is needed to
     *             generate list with favorite recipes
     * @return model Spring UI Model with added attributes
     */
    private Model fillModelWithRecipesFavoritesAndCategories(
            Model model,
            List<Recipe> recipes,
            User user
    ) {
        model.addAttribute("recipes", recipes);

        // find all favorite recipes for currently logged in user
        // and generate array to pass to model, to see
        // which recipes are favorite and which are not
        List<Recipe> favoriteRecipes =
                recipeService.findFavoriteRecipesForUser(
                        user
                );
        model.addAttribute(
                "favoritesWithNullsForNonFavorites",
                generateFavoritesWithNullsForNonFavoritesList(
                        recipes, favoriteRecipes
                )
        );

        // add categories to model
        model.addAttribute("categories", RecipeCategory.values());

        return model;
    }

    // home page with all recipes
    @RequestMapping(RECIPES_HOME_PAGE)
    public String homePageWithAllRecipes(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        model = fillModelWithRecipesFavoritesAndCategories(
                model,
                recipeService.findAll(),
                user
        );
        return INDEX_TEMPLATE;
    }

    // filter recipes by categories
    @RequestMapping(value = RECIPES_HOME_PAGE, params = "category")
    public String filterByCategory(
            @RequestParam ("category") String categoryName,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        model = fillModelWithRecipesFavoritesAndCategories(
                model,
                recipeService.findByRecipeCategoryName(categoryName),
                user
        );
        // add selected category to remember what user
        // pressed and show that on page
        model.addAttribute("selectedCategory",
                RecipeCategory.getRecipeCategoryWithHtmlName(
                        categoryName
                )
        );
        return INDEX_TEMPLATE;
    }

    // filter recipes by description
    @RequestMapping(value = RECIPES_HOME_PAGE, params = "description")
    public String filterByDescription(
            @RequestParam ("description") String description,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        model = fillModelWithRecipesFavoritesAndCategories(
                model,
                recipeService.findByDescriptionContaining(description),
                user
        );
        // add empty category for now, because I don't know how
        // to do otherwise ...
        model.addAttribute("selectedCategory",
                RecipeCategory.getRecipeCategoryWithHtmlName(
                        ""
                )
        );
        return INDEX_TEMPLATE;
    }

    // detail recipe page
    @RequestMapping(RECIPES_HOME_PAGE + "{id}")
    public String detailRecipePage(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal User user
    ) {
        Recipe recipe = recipeService.findOne(id);
        model.addAttribute("recipe", recipe);

        if (recipeService.checkIfRecipeIsFavoriteForUser(
                recipe, user
        )) {
            model.addAttribute(
                    "favoriteImageSrc",
                    "/assets/images/favorited.svg"
            );
            model.addAttribute(
                    "favoriteButtonText",
                        "Remove From Favorites"
                    );
        } else {
            model.addAttribute(
                    "favoriteImageSrc",
                    "/assets/images/favorite.svg"
            );
            model.addAttribute(
                    "favoriteButtonText",
                    "Add To Favorites"
            );
        }
        return DETAIL_TEMPLATE;
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

        String step = "";
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
    @RequestMapping(ADD_NEW_PAGE)
    public String addNewRecipePage(Model model) {
        // empty recipe with ingredients and steps
        // is generated in separate method for
        // testing purposes
        model = addAttributesToModelForBothEditAndAddNewPages(
                generateEmptyRecipeToPassToAddNewPage(),
                model
        );

        return EDIT_TEMPLATE;
    }

    // edit recipe page GET
    // it does not work when "/id/edit" in tests :(
    @RequestMapping(RECIPES_HOME_PAGE +
            EDIT_RECIPE_PAGE_PREFIX + "/{id}")
    public String editRecipePage(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            Model model) {

        Recipe recipe = recipeService.findOne(id);

        // check if user is authorized to edit
        recipeService.checkIfUserCanEditRecipe(
                user, recipe
        );

        // here recipe found in database will be added
        model = addAttributesToModelForBothEditAndAddNewPages(
                recipe,
                model
        );

        return EDIT_TEMPLATE;
    }

    @RequestMapping(value = RECIPES_HOME_PAGE
            + UPDATE_FAVORITES_PAGE_PREFIX
            + "/{id}",
            method = RequestMethod.POST)
    public String updateFavoriteStatusOfRecipe(
            @AuthenticationPrincipal User user,
            @PathVariable("id") Long recipeId,
            RedirectAttributes redirectAttributes
    ) {
        Recipe recipe = recipeService.findOne(recipeId);
        boolean isFavorite = recipeService.updateFavoriteRecipesForUser(
                recipe, user
        );
        if (isFavorite) {
            redirectAttributes.addFlashAttribute(
                    "flash",
                    new FlashMessage(
                            "Recipe '" + recipe.getName() +
                                    "' is added to favorites",
                            FlashMessage.Status.SUCCESS
                    )
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "flash",
                    new FlashMessage(
                            "Recipe '" + recipe.getName() +
                                    "' is removed from favorites",
                            FlashMessage.Status.SUCCESS
                    )
            );
        }
        return "redirect:" + RECIPES_HOME_PAGE;
    }

    // POST request to change saved item
    @RequestMapping(value = POST_SAVE_ADDRESS, method = RequestMethod.POST)
    public String saveRecipe(
            Recipe recipe, // no @Valid here, it comes later
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal User user
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
            // if recipe is being updated, here we make security
            // check
            recipeService.checkIfUserCanEditRecipe(
                    user, recipe
            );
        }
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
            // back to "edit" or "add-new" page
            return "redirect:" + pageFromWherePostReqWasMade;
        }

        // if everything is OK, we save recipes
        // and ingredients
        // because ingredients
        // have foreign key in their table
        // so we have to do that
        // but we have to save them using saved recipe
        // from db, otherwise we'll get OptimisticLockError
        Recipe savedRecipe = recipeService.save(recipe, user);
        savedRecipe.getIngredients().forEach(
                i -> ingredientService.save(i)
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
    @RequestMapping(value = RECIPES_HOME_PAGE
            + DELETE_RECIPE_PAGE_PREFIX
            + "/" + "{id}", method = RequestMethod.POST)
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
