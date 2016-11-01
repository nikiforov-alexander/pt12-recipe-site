package com.techdegree.service;

import com.techdegree.dao.RecipeDao;
import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
import com.techdegree.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ComponentScan
public class RecipeServiceImpl implements RecipeService {
    @Autowired
    private RecipeDao recipeDao;
    @Autowired
    private ItemService itemService;

    @Override
    public List<Recipe> findAll() {
        // will leave this as Florian did, definitely
        // one of TODOs to ask @christherama
        return (List<Recipe>) recipeDao.findAll();
    }

    @Override
    public Recipe findOne(Long id) {
        return recipeDao.findOne(id);
    }

    @Override
    public Recipe save(Recipe recipe, User user) {
        // this code should go
        // in EventHandler class, but I've no much
        // experience with that
        // TODO: create @HandleBeforeCreate EventHandler like in REST
        // that is why I leave all necessary preparations
        // here

        // for each recipe.ingredient.item we take id and
        // get item from database, because I don't know how
        // to pass in thymeleaf whole item object.
        // At this point we are sure that item id will be set
        // because otherwise it will throw error in hasErrors()
        // controller check
        recipe.getIngredients().forEach(
                i -> i.setItem(
                        itemService.findOne(
                                i.getItem().getId()
                        )
                )
        );

        // if recipe.id = null, and we create new recipe,
        // we set new owner based
        // on logged user, then we add it to recipe
        // if recipe.id != null, i.e. we edit recipe
        // then we set owner from database
        if (recipe.getId() == null) {
            recipe.setOwner(
                    user
            );
        } else {
            recipe.setOwner(
                    findOne(recipe.getId()).getOwner()
            );
            // if recipe was favorite we preserve this
            recipe.setFavoriteUsers(
                    findOne(recipe.getId()).getFavoriteUsers()
            );
        }
        return recipeDao.save(recipe);
    }

    @Override
    public void delete(Recipe recipe) {
        recipeDao.delete(recipe);
    }

    @Override
    public List<Recipe> findFavoriteRecipesForUser(User user) {
        return recipeDao.findAllFavoriteRecipesFor(user);
    }



    /**
     * Updates favorite status of {@literal recipe} for
     * {@literal user}. If recipe was already favorite,
     * then it is removed from favorites using
     * {@code recipeDao.removeFavoriteRecipesForUser} method.
     * It it was not
     * @param recipe - {@literal Recipe} which will become
     *               favorite or will be removed from
     *               favorites
     * @param user - {@literal User} for which recipe will be
     *             added as favorite, or removed from favorites
     * @return boolean : true - if recipes becomes favorite,
     * false if recipe is removed from favorites.
     */
    @Override
    public boolean updateFavoriteRecipesForUser(Recipe recipe, User user) {
        if (checkIfRecipeIsFavoriteForUser(recipe, user)) {
            recipeDao.removeFavoriteRecipeForUser(
                    recipe.getId(),
                    user.getId()
            );
            return false;
        } else {
            recipeDao.addFavoriteRecipeForUser(
                    recipe.getId(),
                    user.getId()
            );
            return true;
        }
    }

    /**
     * here we convert favorite recipes for user
     * to List<Long> because I have troubles with
     * writing proper equals method, for now. It is
     * causing more problems that it should. And
     * comparison by Id is pretty enough for me
     * @param recipe {@literal Recipe} which is checked
     *        for being favorite
     * @param user {@literal User} for whom we
     *        check favorite recipes
     * @return boolean : true - if recipe is favorite
     *                 : false - if recipe is not
     */
    @Override
    public boolean checkIfRecipeIsFavoriteForUser(Recipe recipe, User user) {
        // TODO : figure out what to do with equals and hashCode problems
        List<Long> listOfFavoriteRecipesIds =
                recipeDao.findAllFavoriteRecipesFor(user)
                        .stream()
                        .map(Recipe::getId)
                        .collect(Collectors.toList());
        return listOfFavoriteRecipesIds.contains(recipe.getId());
    }

    /**
     * Checks if user is owner or admin to be able to edit recipe
     * @param user : {@code User} which ownership is checked
     * @param recipe : {@code Recipe} which owner is checked
     * @throws AccessDeniedException : if user is non-admin or non-owner
     */
    @Override
    public void checkIfUserCanEditRecipe(User user, Recipe recipe)
            throws AccessDeniedException {
        Recipe recipeFromDb = recipeDao.findOne(recipe.getId());
        if (!recipeFromDb.getOwner().equals(user) &&
                !user.getRole().getName().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException(
                    "Permission Denied"
            );
        }
    }

    @Override
    public List<Recipe> findByRecipeCategoryName(String name) {
        return recipeDao
                .findByRecipeCategory(
                        RecipeCategory.getRecipeCategoryWithHtmlName(name)
                );
    }

    @Override
    public List<Recipe> findByDescriptionContaining(String description) {
        return recipeDao.findByDescriptionContaining(description);
    }

    @Override
    public List<String> findStepsForRecipe(Long recipeId) {
        return recipeDao.findStepsForRecipe(recipeId);
    }

    // one more candidate to include delete(Long id) ...
    // this may be added later if needed. For now it is
    // not so interesting
}
