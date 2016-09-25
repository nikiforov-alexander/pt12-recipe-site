package com.techdegree.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.techdegree.web.WebConstants.RECIPES_HOME_PAGE;

// class with redirects from some pages
// to home page
@Controller
public class IndexRedirectController {

    @RequestMapping("/")
    public String root() {
        return "redirect:" + RECIPES_HOME_PAGE;
    }

    @RequestMapping("/recipes")
    public String redirectFromRecipesWithoutSlash() {
        return "redirect:" + RECIPES_HOME_PAGE;
    }
}
