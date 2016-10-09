package com.techdegree.web.controller;

import com.techdegree.dto.UserDto;
import com.techdegree.exception.UserAlreadyExistsException;
import com.techdegree.model.User;
import com.techdegree.service.CustomUserDetailsService;
import com.techdegree.service.UserService;
import com.techdegree.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.techdegree.web.WebConstants.*;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    // if I put "/login" gives circular path error
    // TODO: figure out what to do with circular error
    @RequestMapping(path = LOGIN_PAGE, method = RequestMethod.GET)
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
        return LOGIN_TEMPLATE;
    }

    // will leave that for now ... ?
    // should ask @christherama about it ...
    @RequestMapping("/access_denied")
    public String accessDenied() {
        return "access_denied";
    }

    // sign up page GET

    @RequestMapping(SIGN_UP_PAGE)
    public String signUpPage(
            Model model
    ) {
        // if model has attribute, we are redirected
        // from sign-up page with errors
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDto());
        }
        return SIGN_UP_TEMPLATE;
    }

    // create new user POST

    @RequestMapping(value = SIGN_UP_PAGE, method = RequestMethod.POST)
    public String registerNewUser(
            @Valid UserDto user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            // if there are errors, we
            // add classic flash attr-s:
            // - user for now with empty passwords ...
            // - flash message on top
            // - bindingResult with errors on fields
            // and redirect back
            user.setPassword("");
            user.setMatchingPassword("");
            redirectAttributes.addFlashAttribute(
                    "user", user
            );
            redirectAttributes.addFlashAttribute(
                    "flash",
                    new FlashMessage(
                            "Oops! some fields have errors ...",
                            FlashMessage.Status.FAILURE
                    )
            );
            redirectAttributes.addFlashAttribute(
                    BINDING_RESULT_PACKAGE_NAME + ".user",
                    bindingResult
            );
            return "redirect:" + SIGN_UP_PAGE;
        }

        // check if user with this username exists
        // i wanted to put this method aside in
        // @ExceptionHandler, but it does not work ...
        // and I've no idea how to fix this :(
        try {
            userService.registerNewUser(user);
        } catch (UserAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage(
                            e.getMessage(),
                            FlashMessage.Status.FAILURE
                    )
            );
            return "redirect:/sign-up";
        }

        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage(
                        "User '" + user.getUsername() +
                                "' was successfully registered",
                        FlashMessage.Status.SUCCESS
                )
        );
        return "redirect:" + RECIPES_HOME_PAGE;
    }

}
