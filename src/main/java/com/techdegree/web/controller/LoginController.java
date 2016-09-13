package com.techdegree.web.controller;

import com.techdegree.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String loginForm(
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("user", new User());
        try {
            Object flash =
                    request.getSession().getAttribute(
                            "flash"
            );
            model.addAttribute("flash", flash);
        } catch (Exception ex) {
            // "flash" session attribute must not exist...do nothing and proceed normally
        }
        return "login";
    }

    // will leave that for now ... ?
    // should ask @christherama about it ...
    @RequestMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }
}
