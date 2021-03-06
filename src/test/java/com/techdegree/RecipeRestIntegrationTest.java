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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;

import static com.techdegree.testing_shared_helpers.IterablesConverterHelper.getSizeOfIterable;
import static com.techdegree.web.WebConstants.RECIPES_REST_PAGE;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url = jdbc:h2:./database/test-RecipeRestIntegrationTest-recipes;DB_CLOSE_ON_EXIT=FALSE"
)
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

        // Arrange : get Admin user
        User user = (User) userService.loadUserByUsername("sa");

        assertThat(
                "user is admin",
                user.getRole().getName(),
                is("ROLE_ADMIN")
        );

        // Act and Assert:
        // When POST request to RECIPES_REST_PAGE is made
        // by admin with empty JSON with one empty step:
        // {
        //  "steps" : [""]
        // }
        // Then :
        // - status should be bad request
        // - 11 errors should be returned:
        //   @NotNull, @NotEmpty for "name"
        //   @NotNull, @NotEmpty for "description"
        //   @NotNull for "recipeCategory"
        //   @NotNull, @NotEmpty for "photoUrl"
        //   @NotNull, @NotEmpty for "preparationTime"
        //   @NotNull, @NotEmpty for "cookTime"
        //   @NotEmpty for first step
        mockMvc.perform(
                post(
                        BASE_URL + RECIPES_REST_PAGE
                ).contentType(contentType)
                .with(
                        SecurityMockMvcRequestPostProcessors.user(
                                user
                        )
                )
                .content("{\"steps\":[\"\"]}")
        ).andDo(print())
        .andExpect(
                status().isBadRequest()
        )
        .andExpect(
                jsonPath(
                        "$.errors",
                        hasSize(12)
                )
        );
    }

    @Test
    public void postNewRecipeWithValidFieldsAndLoggedUserShouldCreateNewRecipeWithOwner()
            throws Exception {
        // Arrange : mockMvc is arranged

        // Arrange : create test Recipe with 1 step
        Recipe testRecipe = new Recipe.RecipeBuilder(null)
                .withVersion(null)
                .withName("test name")
                .withDescription("test description")
                .withRecipeCategory(RecipeCategory.BREAKFAST)
                .withPhotoUrl("test photo url")
                .withPreparationTime("test prep time")
                .withCookTime("test cook time")
                .build();
        testRecipe.addStep("step 1");

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

        assertThat(
                "logged user owns the recipe",
                // NOTE: numberOfRecipesBeforeReq + 1 is equal to "id" of
                // newly created recipe
                recipeDao.findOne(
                        (long) numberOfRecipesBeforeReq + 1
                ),
                hasProperty(
                        "owner", equalTo(user)
                )
        );

        assertThat(
                "recipe has step 'step 1'",
                recipeDao.findStepsForRecipe(
                        (long) numberOfRecipesBeforeReq + 1
                ),
                containsInAnyOrder(
                        "step 1"
                )
        );
    }

    @Test
    public void notOwnerUserWithRoleUserCannotChangeRecipe()
            throws Exception {
        // Arrange : mockMvc is arranged

        // Arrange : get first Recipe, and change
        // its name, ingredients and steps
        Recipe firstRecipe = recipeDao.findOne(1L);
        firstRecipe.setName("new name");
        firstRecipe.setIngredients(new ArrayList<>());
        firstRecipe.setSteps(new ArrayList<>());

        // Arrange : get Test non-admin user from db
        User user = (User) userService.loadUserByUsername("ad");

        assertThat(
                "user is not admin",
                user.getRole().getName(),
                is("ROLE_USER")
        );
        assertThat(
                "user is not owner",
                firstRecipe.getOwner(),
                not(
                        is(user)
                )
        );

        // Arrange : calculate number of recipes before req
        int numberOfRecipesBeforeReq =
                getSizeOfIterable(
                        recipeDao.findAll()
                );

        // Act and Assert:
        // When PUT request with valid testRecipe to
        // and with test non-admin, non-owner user
        // RECIPES_REST_PAGE is made,
        // Then :
        // - status should be FORBIDDEN
        mockMvc.perform(
                put(
                        BASE_URL + RECIPES_REST_PAGE +
                                "/" + firstRecipe.getId()
                ).contentType(contentType)
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                        user
                                )
                        )
                        .content(
                                recipeJacksonTester.write(
                                        firstRecipe
                                ).getJson()
                        )
        ).andDo(print())
        .andExpect(
                status().isForbidden()
        );

        assertThat(
                "Number of recipes should stay same",
                getSizeOfIterable(
                        recipeDao.findAll()
                ),
                is(numberOfRecipesBeforeReq)
        );

    }

    @Test
    public void adminUserNonOwnerCanChangeRecipe()
            throws Exception {
        // Arrange : mockMvc is arranged

        // Arrange : get first Recipe, and change
        // its name, ingredients and steps
        // because otherwise we get lazy instantiation error
        // TODO: figure out lazy instantiation error
        Recipe firstRecipe = recipeDao.findOne(1L);
        firstRecipe.setName("new name for admin test");
        firstRecipe.setIngredients(new ArrayList<>());
        firstRecipe.setSteps(new ArrayList<>());
        long versionOfRecipe = firstRecipe.getVersion();

        // Arrange : get Admin user
        User user = (User) userService.loadUserByUsername("sa");

        assertThat(
                "user is admin",
                user.getRole().getName(),
                is("ROLE_ADMIN")
        );
        assertThat(
                "user is not owner",
                firstRecipe.getOwner(),
                not(
                        is(user)
                )
        );

        // Act and Assert:
        // When PUT request with valid first recipe
        // with user admin to
        // RECIPES_REST_PAGE + "/1" is made,
        // Then :
        // - status should be no content
        mockMvc.perform(
                put(
                        BASE_URL + RECIPES_REST_PAGE +
                                "/" + firstRecipe.getId()
                ).contentType(contentType)
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                        user
                                )
                        )
                        .content(
                                recipeJacksonTester.write(
                                        firstRecipe
                                ).getJson()
                        )
        ).andDo(print())
                .andExpect(
                        status().isNoContent()
                );

        // TODO : figure out why we can't compare recipes using equals
        //assertThat(
        //        "recipe should change",
        //        recipeDao.findOne(1L),
        //        is(firstRecipe)
        //);

        assertThat(
                "Recipe Version should change",
                recipeDao.findOne(1L),
                hasProperty(
                        "version",
                        is(versionOfRecipe + 1)
                )
        );
    }

    @Test
    public void recipeOwnerCanChangeRecipe()
            throws Exception {
        // Arrange : mockMvc is arranged

        // Arrange : get first Recipe, and change
        // its name, ingredients and steps
        // because otherwise we get lazy instantiation error
        // TODO: figure out lazy instantiation error
        Recipe firstRecipe = recipeDao.findOne(1L);
        firstRecipe.setName("new name for owner test");
        firstRecipe.setIngredients(new ArrayList<>());
        firstRecipe.setSteps(new ArrayList<>());
        long versionOfRecipe = firstRecipe.getVersion();

        // Arrange : get user: recipe owner
        User user = (User) userService.loadUserByUsername("jd");

        assertThat(
                "user is normal user",
                user.getRole().getName(),
                is("ROLE_USER")
        );
        assertThat(
                "user is owner",
                firstRecipe.getOwner(),
                is(user)
        );

        // Act and Assert:
        // When PUT request with valid first recipe
        // with user that is owner
        // RECIPES_REST_PAGE + "/1" is made,
        // Then :
        // - status should be no content
        mockMvc.perform(
                put(
                        BASE_URL + RECIPES_REST_PAGE +
                                "/" + firstRecipe.getId()
                ).contentType(contentType)
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                        user
                                )
                        )
                        .content(
                                recipeJacksonTester.write(
                                        firstRecipe
                                ).getJson()
                        )
        ).andDo(print())
        .andExpect(
                status().isNoContent()
        );

        assertThat(
                "Recipe Version should change",
                recipeDao.findOne(1L),
                hasProperty(
                        "version",
                        is(versionOfRecipe + 1)
                )
        );
    }

    @Test
    public void recipeCanBeDeletedByOwner() throws Exception {
        // Arrange: mockMvc with webAppContext is arranged

        // Arrange: get owner of recipe from service
        User owner = (User) userService.loadUserByUsername("jd");

        assertThat(
                "user is non-admin",
                owner.getRole(),
                hasProperty(
                        "name", equalTo("ROLE_USER")
                )
        );

        // Arrange: calculate number of recipes before
        // add/delete test
        int numberOfRecipesBeforeAddDelete =
                getSizeOfIterable(
                        recipeDao.findAll()
                );

        // Arrange: add new recipe to be deleted afterwards
        // and set owner of the recipe to logged user
        Recipe recipeToBeSavedAndDeleted =
                new Recipe.RecipeBuilder(null)
                .withVersion(null)
                .withName("test name")
                .withDescription("test description")
                .withRecipeCategory(RecipeCategory.BREAKFAST)
                .withPhotoUrl("test photo url")
                .withPreparationTime("test prep time")
                .withCookTime("test cook time")
                .build();
        recipeToBeSavedAndDeleted.setOwner(owner);
        Recipe savedRecipe = recipeDao.save(recipeToBeSavedAndDeleted);

        // Act and Assert:
        // When DELETE request is made to newly added recipe
        // with JSON from savedRecipe
        // Then :
        // - status should be no content
        mockMvc.perform(
                delete(
                        BASE_URL + RECIPES_REST_PAGE
                                + "/" + savedRecipe.getId()
                ).contentType(contentType)
                .with(
                        SecurityMockMvcRequestPostProcessors.user(
                                owner
                        )
                )
                .content(
                        recipeJacksonTester.write(
                                savedRecipe
                        ).getJson()
                )
        ).andDo(print())
        .andExpect(
                status().isNoContent()
        );

        // Assert that number of recipes stayed same
        assertThat(
                getSizeOfIterable(
                        recipeDao.findAll()
                ),
                is(numberOfRecipesBeforeAddDelete)
        );
    }

    @Test
    public void recipeCanBeDeletedByAdmin() throws Exception {
        // Arrange: mockMvc with webAppContext is arranged

        // Arrange: get admin
        User admin = (User) userService.loadUserByUsername("sa");

        assertThat(
                "user is admin",
                admin.getRole(),
                hasProperty(
                        "name", equalTo("ROLE_ADMIN")
                )
        );

        // Arrange: calculate number of recipes before
        // add/delete test
        int numberOfRecipesBeforeAddDelete =
                getSizeOfIterable(
                        recipeDao.findAll()
                );

        // Arrange: add new recipe to be deleted afterwards
        // and set owner of the recipe to logged user
        Recipe recipeToBeSavedAndDeleted =
                new Recipe.RecipeBuilder(null)
                        .withVersion(null)
                        .withName("test name")
                        .withDescription("test description")
                        .withRecipeCategory(RecipeCategory.BREAKFAST)
                        .withPhotoUrl("test photo url")
                        .withPreparationTime("test prep time")
                        .withCookTime("test cook time")
                        .build();
        recipeToBeSavedAndDeleted.setOwner(
                (User) userService.loadUserByUsername("jd")
        );
        Recipe savedRecipe = recipeDao.save(recipeToBeSavedAndDeleted);

        // Act and Assert:
        // When DELETE request is made to newly added recipe
        // with JSON from savedRecipe
        // Then :
        // - status should be no content
        mockMvc.perform(
                delete(
                        BASE_URL + RECIPES_REST_PAGE
                                + "/" + savedRecipe.getId()
                ).contentType(contentType)
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                        admin
                                )
                        )
                        .content(
                                recipeJacksonTester.write(
                                        savedRecipe
                                ).getJson()
                        )
        ).andDo(print())
                .andExpect(
                        status().isNoContent()
                );

        // Assert that number of recipes stayed same
        assertThat(
                getSizeOfIterable(
                        recipeDao.findAll()
                ),
                is(numberOfRecipesBeforeAddDelete)
        );
    }

    @Test(expected = NestedServletException.class)
    public void recipeCannotBeDeletedByNonAdminNonOwner() throws Exception {
        // Arrange: mockMvc with webAppContext is arranged

        // Arrange: get admin
        User nonOwnerNonAdmin = (User) userService.loadUserByUsername("ad");

        assertThat(
                "user is non-admin",
                nonOwnerNonAdmin.getRole(),
                hasProperty(
                        "name", equalTo("ROLE_USER")
                )
        );

        // Act and Assert:
        // When DELETE request is made to first recipe
        // with JSON from savedRecipe
        mockMvc.perform(
                delete(
                        BASE_URL + RECIPES_REST_PAGE
                                + "/" + 1L
                ).contentType(contentType)
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                        nonOwnerNonAdmin
                                )
                        )
        ).andDo(print());

        // Assert: Then Exception should be thrown
    }
}