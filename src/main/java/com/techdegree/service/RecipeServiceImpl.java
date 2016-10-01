package com.techdegree.service;

import com.techdegree.dao.RecipeDao;
import com.techdegree.model.Recipe;
import com.techdegree.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

    // one more candidate to include delete(Long id) ...
    // this may be added later if needed. For now it is
    // not so interesting
}
