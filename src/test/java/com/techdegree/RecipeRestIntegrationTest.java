package com.techdegree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techdegree.dao.RecipeDao;
import com.techdegree.model.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.techdegree.web.WebConstants.RECIPES_REST_PAGE;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:./test-RecipeRestIntegrationTest.properties")
public class RecipeRestIntegrationTest {

    // will be something like "/api/v1":
    // is set from properties file
    @Value("${spring.data.rest.base-path}")
    private String BASE_URL;

    private JacksonTester<Recipe> recipeJacksonTester;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // we autowire DAO-s here because
    // REST is created through DAOs, not
    // services
    @Autowired
    private RecipeDao recipeDao;

    private MockMvc mockMvc;

    private MediaType contentType =
            new MediaType(
                    MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype()
            );

    @Before
    public void setUp() throws Exception {
        // initialize JacksonTester "json"
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
        // setup mockMvc with web app context
        mockMvc = MockMvcBuilders.webAppContextSetup(
                webApplicationContext
        ).build();
    }

    // private methods

    // Tests checking that all DAOs are good, i.e.
    // no referential integrity violations

    @Test
    public void restRecipesPageCanBeRendered()
            throws Exception {
        mockMvc.perform(
                get(BASE_URL + RECIPES_REST_PAGE)
        ).andDo(print())
        .andExpect(
            status().isOk()
        );
    }

    // POST requests tests

    @Test
    public void postEmptyJsonCreatingNewRecipeShouldReturnValidationErrors()
            throws Exception {
        // Arrange : mockMvc with webAppContext is set up

        // Act and Assert:
        // When POST request to RECIPES_REST_PAGE is made
        // with empty JSON "{}"
        // Then :
        // - status should be bad request
        // - 11 errors should be returned:
        //   @NotNull, @NotEmpty for "name"
        //   @NotNull, @NotEmpty for "description"
        //   @NotNull for "recipeCategory"
        //   @NotNull, @NotEmpty for "photoUrl"
        //   @NotNull, @NotEmpty for "preparationTime"
        //   @NotNull, @NotEmpty for "cookTime"
        mockMvc.perform(
                post(
                        BASE_URL + RECIPES_REST_PAGE
                ).contentType(contentType)
                .content("{}")
        ).andDo(print())
        .andExpect(
                status().isBadRequest()
        )
        .andExpect(
                jsonPath(
                        "$.errors",
                        hasSize(11)
                )
        );
    }
}