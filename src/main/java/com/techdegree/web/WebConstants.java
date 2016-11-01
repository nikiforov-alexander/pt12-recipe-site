package com.techdegree.web;

public final class WebConstants {

    // URI-s

    public static final String RECIPES_HOME_PAGE = "/recipes/";

    public static final String POST_SAVE_ADDRESS = RECIPES_HOME_PAGE + "save";

    public static final String SIGN_UP_PAGE = "/sign-up";
    public static final String LOGIN_PAGE = "/login";
    public static final String PROFILE_PAGE = "/profile";
    public static final String ADD_NEW_PAGE = RECIPES_HOME_PAGE + "add-new";

    public static final String UPDATE_FAVORITES_PAGE_PREFIX =
            "/update-favorite-status-of-recipe";
    public static final String EDIT_RECIPE_PAGE_PREFIX =
            "/edit";
    public static final String DELETE_RECIPE_PAGE_PREFIX =
            "/delete";

    public static String getDeleteRecipePageWithId(String id) {
        return RECIPES_HOME_PAGE +
                DELETE_RECIPE_PAGE_PREFIX +
                "/" + id;
    }

    public static String
    getEditRecipePageWithId(String id) {
        return RECIPES_HOME_PAGE + EDIT_RECIPE_PAGE_PREFIX + "/" + id;
    }

    public static String
    updateFavoriteStatusPageWithId(String id) {
                return RECIPES_HOME_PAGE +
                        UPDATE_FAVORITES_PAGE_PREFIX +
                        "/" + id;
    }

    // REST API URI-s

    public static final String RECIPES_REST_PAGE = "/recipes";
    public static final String INGREDIENTS_REST_PAGE = "/ingredients";
    public static final String ITEMS_REST_PAGE = "/items";


    // Thymeleaf templates

    public static final String INDEX_TEMPLATE = "index";
    public static final String DETAIL_TEMPLATE = "detail";
    public static final String LOGIN_TEMPLATE = "login";
    public static final String PROFILE_TEMPLATE = "profile";
    public static final String SIGN_UP_TEMPLATE = "signup";
    public static final String EDIT_TEMPLATE = "edit";

    // Binding result full package name

    public static final String BINDING_RESULT_PACKAGE_NAME =
            "org.springframework.validation.BindingResult";
}
