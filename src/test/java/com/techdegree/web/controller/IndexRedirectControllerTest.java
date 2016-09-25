package com.techdegree.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class IndexRedirectControllerTest {

    @InjectMocks
    private IndexRedirectController indexRedirectController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(
                indexRedirectController
        ).build();
    }

    @Test
    public void rootOnePageSlashRedirectsBackToRecipesPage()
            throws Exception {
        // Arrange: mockMvc with IndexRedirectController is
        // set up

        // Act and Assert:
        // When get request to root "/" is made
        // then user should be redirected to "/recipes/"
        mockMvc.perform(
                get("/")
        ).andDo(print())
                .andExpect(
                        redirectedUrl("/recipes/")
                );
    }

    @Test
    public void recipesWithoutSlashRedirectsBackToRecipesPage()
            throws Exception {
        // Arrange: mockMvc with IndexRedirectController is
        // set up

        // Act and Assert:
        // When get request to root "/recipes" is made
        // then user should be redirected to "/recipes/"
        mockMvc.perform(
                get("/recipes")
        ).andDo(print())
        .andExpect(
                redirectedUrl("/recipes/")
        );
    }
}