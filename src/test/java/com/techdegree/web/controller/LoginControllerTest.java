package com.techdegree.web.controller;

import com.techdegree.dto.UserDto;
import com.techdegree.exception.UserAlreadyExistsException;
import com.techdegree.model.User;
import com.techdegree.service.CustomUserDetailsService;
import com.techdegree.web.FlashMessage;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.techdegree.web.WebConstants.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
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
        // When request to "/login/" is made
        // Hell knows why but if I made request to
        // "/login", Circular error appear
        // TODO: chunk circular "/login" "/login/" problem
        // and push on Stack/Git/Slack
        // Then:
        // - status should be OK
        // - view name "login"
        // - model has no attribute "flash" in the absence
        //   of session
        // - model has to have attribute, new User
        mockMvc.perform(
                get(LOGIN_PAGE + "/")
        ).andDo(print())
        .andExpect(
                status().isOk()
        )
        .andExpect(
                view().name(LOGIN_TEMPLATE)
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
                        view().name(LOGIN_TEMPLATE)
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
        // Arrange : arrange mockMvc with LoginController

        // Act: When make GET request to "sign-up" page is made
        // Assert:
        // Then :
        // - status should be OK
        // - view must be of name SIGN_UP_PAGE
        // - model should have only one attribute user
        //   in the absence of flash got with
        //   session
        mockMvc.perform(
                get(SIGN_UP_PAGE)
        ).andDo(print())
        .andExpect(
                status().isOk()
        )
        .andExpect(
                view().name(SIGN_UP_TEMPLATE)
        )
        .andExpect(
                model().attributeDoesNotExist(
                        "flash"
                )
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
                get(SIGN_UP_PAGE)
                .flashAttr("user", user)
        ).andDo(print())
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        view().name(SIGN_UP_TEMPLATE)
                )
                .andExpect(
                        model().attribute(
                                "user", user
                        )
                );
    }

    @Test
    public void postWithInvalidNullUserFieldsIsRedirectedBackToSignUp()
            throws Exception {
        // Arrange: mockMvc is arranged with LoginController

        // When post request is made with all null fields
        // Then :
        // - status should be 3xx
        // - redirected URL should be sign-up page
        // - flash should be sent with error status
        // - binding result sent should have 8 errors
        //   : 4 for @NotNull and 3 for @NotEmpty
        //   unfortunately @Pattern does not throw error
        //   here, however I tested it separately in UserDtoTest
        //   and also one global error, because
        //   PasswordMatchesValidator should not work
        //   when user.password is null.
        // - flash attribute "user" should exists
        //   and should have user.password and
        //   user.matchingPassword empty
        mockMvc.perform(
                post(SIGN_UP_PAGE)
        ).andDo(print())
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(SIGN_UP_PAGE)
                )
                .andExpect(
                        flash().attribute(
                                "flash",
                                hasProperty(
                                        "status",
                                        equalTo(
                                                FlashMessage.Status.FAILURE
                                        )
                                )
                        )
                )
                .andExpect(
                        flash().attribute(
                                BINDING_RESULT_PACKAGE_NAME + ".user",
                                hasProperty(
                                        "fieldErrorCount",
                                        equalTo(7)
                                )
                        )
                )
                .andExpect(
                        flash().attribute(
                                BINDING_RESULT_PACKAGE_NAME + ".user",
                                hasProperty(
                                        "globalErrorCount",
                                        equalTo(1)
                                )
                        )
                )
                .andExpect(
                        flash().attributeExists("user")
                )
                .andExpect(
                        flash().attribute(
                                "user",
                                hasProperty(
                                        "password",
                                        isEmptyString()
                                )
                        )
                )
                .andExpect(
                        flash().attribute(
                                "user",
                                hasProperty(
                                        "matchingPassword",
                                        isEmptyString()
                                )
                        )
                );
    }

    @Test
    public void postWithUsernameThatAlreadyExistsRedirectsBack()
            throws Exception {
        // Arrange: mockMvc is arranged with LoginController

        // Arrange: throw UserAlreadyExistsException
        // when registerNewUser is called
        when(userService.registerNewUser(any()))
                .thenThrow(
                        UserAlreadyExistsException.class
                );

        // When post request is made with all valid fields
        // but username already exists, we get redirected
        // back with just "flash"

        // Then :
        // - status should be 3xx
        // - redirected URL should be sign-up page
        // - flash should be sent with error status
        // - should be no "user" attribute
        mockMvc.perform(
                post(SIGN_UP_PAGE)
                .param("name", "name")
                .param("username", "username")
                .param("password", "qwertyZ1")
                .param("matchingPassword", "qwertyZ1")
        ).andDo(print())
        .andExpect(
                status().is3xxRedirection()
        )
        .andExpect(
                redirectedUrl(SIGN_UP_PAGE)
        )
        .andExpect(
                flash().attribute(
                        "flash",
                        hasProperty(
                                "status",
                                equalTo(
                                        FlashMessage.Status.FAILURE
                                )
                        )
                )
        )
        .andExpect(
                flash().attributeCount(1)
        );

        // verify mock interactions
        verify(userService).registerNewUser(any());
    }

    @Test
    public void postSignUpRequestCreatesNewUserAndRedirectsToLoginPage()
            throws Exception {
        // Arrange: mockMvc is arranged with LoginController

        // Arrange: register new user
        // when registerNewUser is called
        when(userService.registerNewUser(any()))
                .thenReturn(
                       any(User.class)
                );

        // Act: When post request is made with all valid fields
        // and username does not exists

        // Assert: Then
        // - status should be 3xx
        // - redirected URL should be LOGIN_PAGE
        // - flash should be sent with success status
        mockMvc.perform(
                post(SIGN_UP_PAGE)
                        .param("name", "name")
                        .param("username", "username")
                        .param("password", "qwertyZ1")
                        .param("matchingPassword", "qwertyZ1")
        ).andDo(print())
                .andExpect(
                        status().is3xxRedirection()
                )
                .andExpect(
                        redirectedUrl(LOGIN_PAGE)
                )
                .andExpect(
                        flash().attribute(
                                "flash",
                                hasProperty(
                                        "status",
                                        equalTo(
                                                FlashMessage.Status.SUCCESS
                                        )
                                )
                        )
                )
                .andExpect(
                        flash().attributeCount(1)
                );

        // verify mock interactions
        verify(userService).registerNewUser(any());
    }
}