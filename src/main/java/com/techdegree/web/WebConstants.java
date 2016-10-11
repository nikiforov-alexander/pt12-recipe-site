package com.techdegree.web;

public final class WebConstants {

    // URI-s

    public static final String RECIPES_HOME_PAGE = "/recipes/";
    public static final String SIGN_UP_PAGE = "/sign-up";
    public static final String LOGIN_PAGE = "/login";
    public static final String PROFILE_PAGE = "/profile";

    // REST API URI-s

    public static final String RECIPES_REST_PAGE = "/recipes";
    public static final String INGREDIENTS_REST_PAGE = "/ingredients";
    public static final String STEPS_REST_PAGE = "/steps";
    public static final String ITEMS_REST_PAGE = "/items";


    // Thymeleaf templates

    public static final String INDEX_TEMPLATE = "index";
    public static final String DETAIL_TEMPLATE = "detail";
    public static final String LOGIN_TEMPLATE = "login";
    public static final String PROFILE_TEMPLATE = "profile";
    public static final String SIGN_UP_TEMPLATE = "signup";

    // Binding result full package name

    public static final String BINDING_RESULT_PACKAGE_NAME =
            "org.springframework.validation.BindingResult";
}
