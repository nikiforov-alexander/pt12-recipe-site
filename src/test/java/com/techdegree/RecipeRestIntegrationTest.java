package com.techdegree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techdegree.dao.RecipeDao;
import com.techdegree.model.Recipe;
import com.techdegree.model.RecipeCategory;
import com.techdegree.model.User;
import com.techdegree.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.techdegree.testing_shared_helpers.IterablesConverterHelper.getSizeOfIterable;
import static com.techdegree.web.WebConstants.RECIPES_REST_PAGE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
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

    @Autowired
    private UserService userService;

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

    @Test
    public void postNewRecipeWithValidFieldsAndLoggedUserShouldCreateNewRecipeWithOwner()
            throws Exception {
        // Arrange : mockMvc is arranged

        // Arrange : create test Recipe
        Recipe testRecipe = new Recipe.RecipeBuilder(null)
                .withVersion(null)
                .withName("test name")
                .withDescription("test description")
                .withRecipeCategory(RecipeCategory.BREAKFAST)
                .withPhotoUrl("test photo url")
                .withPreparationTime("test prep time")
                .withCookTime("test cook time")
                .build();

        // Arrange : calculate number of recipes before req
        int numberOfRecipesBeforeReq =
                getSizeOfIterable(
                        recipeDao.findAll()
                );
        // Arrange : get Test user from db
        User user = (User) userService.loadUserByUsername("jd");

        // Act and Assert:
        // When POST request with valid testRecipe to
        // and with test user, to set owner
        // RECIPES_REST_PAGE is made,
        // Then :
        // - status should be created
        mockMvc.perform(
                post(
                        BASE_URL + RECIPES_REST_PAGE
                ).contentType(contentType)
                .with(
                        SecurityMockMvcRequestPostProcessors.user(
                                user
                        )
                )
                .content(
                        recipeJacksonTester.write(
                                testRecipe
                        ).getJson()
                )
        ).andDo(print())
        .andExpect(
                status().isCreated()
        );

        // Assert that number of recipes increased
        assertThat(
                getSizeOfIterable(
                        recipeDao.findAll()
                ),
                is(numberOfRecipesBeforeReq + 1)
        );

        // Assert that user owns the recipe
        // NOTE: numberOfRecipesBeforeReq + 1 is equal to "id" of
        // newly created recipe
        assertThat(
                recipeDao.findOne(
                        (long) numberOfRecipesBeforeReq + 1
                ),
                hasProperty(
                        "owner", equalTo(user)
                )
        );
    }
}