package com.techdegree.web.controller;

import com.techdegree.dto.UserDto;
import com.techdegree.model.User;
import com.techdegree.service.CustomUserDetailsService;
import com.techdegree.web.FlashMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

    @Mock
    private CustomUserDetailsService userService;

    @InjectMocks
    private LoginController loginController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(
                loginController
        ).build();
    }

    @Test
    public void loginFromHasAttributeUser() throws Exception {
        // Arrange: mockMvc is arranged with
        // LoginController

        // Act and Assert:
        // When request to "/login" is made
        // Then:
        // - status should be OK
        // - view name "login"
        // - model has no attribute "flash" in the absence
        //   of session
        // - model has to have attribute, new User
        mockMvc.perform(
                get("/login/")
        ).andDo(print())
        .andExpect(
                status().isOk()
        )
        .andExpect(
                view().name("login")
        )
        .andExpect(
                model().attributeDoesNotExist("flash")
        )
        .andExpect(
                // when I leave any(User.class),
                // it gives error ...
                model().attribute(
                        "user", new User()
                )
        );
    }

    @Test
    public void loginPageWithFlashAttributeInSessionGivesPageWithFlash()
            throws Exception {
        // Arrange: mockMvc is arranged with
        // LoginController

        // Arrange: create test flash attribute
        FlashMessage flashMessage =
                new FlashMessage("message",
                        FlashMessage.Status.FAILURE
                );

        // Act and Assert:
        // When request to "/login" is made
        // with session attribute "flash"
        // Then:
        // - status should be OK
        // - view name "login"
        // - model has to have attribute, new User
        // - model should have flash message from
        //   session attribute
        mockMvc.perform(
                get("/login/")
                .sessionAttr("flash", flashMessage)
        ).andDo(print())
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name("login")
                )
                .andExpect(
                        model().attribute(
                                "flash",
                                is(flashMessage)
                        )
                )
                .andExpect(
                        // when I leave any(User.class),
                        // it gives error ...
                        model().attribute(
                                "user",
                                new User()
                        )
                );
    }

    @Test
    public void signUpShouldBePageRenderedWithUserAttribute()
            throws Exception {
        mockMvc.perform(
                get("/sign-up")
        ).andDo(print())
        .andExpect(
                status().isOk()
        )
        .andExpect(
                view().name("signup")
        )
        .andExpect(
                model().attribute(
                        "user",
                        new UserDto()
                )
        );
    }

    @Test
    public void signUpPageCanBeRenderedWithUserFromRedirectAttributes()
            throws Exception {
        // Arrange: mockMvc with LoginController is arranged

        // Arrange some UserDto passed with flash
        // attributes
        UserDto user = new UserDto();
        user.setUsername("jd");

        // When request to "/sign-up" is made
        // Then :
        // - status should be OK
        // - view name "signup"
        // - model attribute "user" should be
        //   arranged user
        mockMvc.perform(
                get("/sign-up")
                .flashAttr("user", user)
        ).andDo(print())
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name("signup")
                )
                .andExpect(
                        model().attribute(
                                "user", user
                        )
                );
    }
}