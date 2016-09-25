package com.techdegree.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

// class with redirects from some pages
// to home page
@Controller
public class IndexRedirectController {

    @RequestMapping("/")
    public String root() {
        return "redirect:/recipes/";
    }

    @RequestMapping("/recipes")
    public String redirectFromRecipesWithoutSlash() {
        return "redirect:/recipes/";
    }
}
