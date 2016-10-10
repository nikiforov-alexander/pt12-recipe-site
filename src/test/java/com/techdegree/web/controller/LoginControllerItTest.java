package com.techdegree.web.controller;

import com.techdegree.dto.UserDto;
import com.techdegree.service.UserService;
import com.techdegree.web.FlashMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.techdegree.web.WebConstants.LOGIN_PAGE;
import static com.techdegree.web.WebConstants.SIGN_UP_PAGE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:./test-LoginControllerItTest.properties")
public class LoginControllerItTest {

    // constants

    @LocalServerPort
    private int actualPortNumber;

    private String BASE_URL;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(
                applicationContext
        ).build();
        // here i will try not to use
        // BASE_URL. As it turned out, it does not matter
        // apparently
        BASE_URL = "http://localhost:" + actualPortNumber;
    }

    @Test
    public void registeringValidNewUserWorks() throws Exception {
        // Arrange: mockMvc with webAppContext is set up
        // for integration test

        // Arrange: create test UserDto to be created
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setUsername("tu");
        userDto.setPassword("qwertyZ1");
        userDto.setMatchingPassword("qwertyZ1");

        // Arrange: calculate number of users before request
        int numberOfUsersBeforeSignUpRequest =
                userService.findAll().size();

        // Act and Assert:
        // When POST request is made to "sign-up" page
        // with valid UserDto data
        // Then:
        // - status should be 3xx redirection
        // - redirectedUrl should be LOGIN_PAGE
        // - one flash attribute with SUCCESS status
        //   should be sent
        mockMvc.perform(
                post(SIGN_UP_PAGE)
                .param("name", userDto.getName())
                .param("username", userDto.getUsername())
                .param("password", userDto.getPassword())
                .param("matchingPassword", userDto.getMatchingPassword())
        ).andDo(print())
        .andExpect(
                status().is3xxRedirection()
        )
        .andExpect(
                redirectedUrl(LOGIN_PAGE)
        )
        .andExpect(
                flash().attributeCount(1)
        )
        .andExpect(
                flash().attribute(
                        "flash",
                        hasProperty(
                                "status",
                                is(FlashMessage.Status.SUCCESS)
                        )
                )
        );

        // Assert that new user was created
        assertThat(
                userService.findAll().size(),
                is(numberOfUsersBeforeSignUpRequest + 1)
        );
    }

    @Test
    public void invalidPostRequestWillNotCreateUser() throws Exception {
        // Arrange: mockMvc with webAppContext is set up
        // for integration test

        // Arrange: calculate number of users before request
        int numberOfUsersBeforeSignUpRequest =
                userService.findAll().size();

        // Act and Assert:
        // When POST request is made to "sign-up" page
        // with valid UserDto data
        // Then:
        // - status should be 3xx redirection
        // - redirectedUrl should be recipes home page
        // - three flash attributes
        //   should be sent
        // - "user" flash attribute should have
        //   empty "password" and "matchingPassword"
        //   fields
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
                        flash().attributeCount(3)
                )
                .andExpect(
                        flash().attribute(
                                "flash",
                                hasProperty(
                                        "status",
                                        is(FlashMessage.Status.FAILURE)
                                )
                        )
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

        // Assert that new user was NOT created
        assertThat(
                userService.findAll().size(),
                is(numberOfUsersBeforeSignUpRequest)
        );
    }
}