package com.techdegree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techdegree.dao.IngredientDao;
import com.techdegree.dao.RecipeDao;
import com.techdegree.model.Ingredient;
import com.techdegree.model.Recipe;
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

import static com.techdegree.testing_shared_helpers.GenericJsonWithLinkGenerator.addCustomProperty;
import static com.techdegree.testing_shared_helpers.IterablesConverterHelper.getSizeOfIterable;
import static com.techdegree.web.WebConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:./test-IngredientRestIntegrationTest.properties")
public class IngredientRestIntegrationTest {
    // will be something like "/api/v1":
    // is set from properties file
    @Value("${spring.data.rest.base-path}")
    private String BASE_URL;

    private JacksonTester<Ingredient> ingredientJacksonTester;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // we autowire DAO-s here because
    // REST is created through DAOs, not
    // services
    @Autowired
    private IngredientDao ingredientDao;

    @Autowired
    private UserService userService;

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

    /**
     * Generates String JSON of {@code Ingredient} with
     * @param ingredient Ingredient, which values will be set
     *             to JSON properties
     * @param itemUrl String, item url that is
     *                  added to the JSON in order
     *                  for ingredients to be created with
     *                  some item
     * @param recipeUrl String, recipe url that is
     *                  added to the JSON in order
     *                  for ingredients to be created with
     *                  some recipe
     * @return String JSON of Ingredient:
     * for example:
     * @{code
     *  {
     *      "id" : "null",
     *      "version" : "null",
     *      "item" : "/api/v1/items/1"
     *      "condition" : "condition"
     *      "quantity" : "quantity"
     *      "recipe" : "/api/v1/recipes/1"
     *  }
     * }
     */
    private String generateIngredientJsonWithItemAndRecipe(
            Ingredient ingredient,
            String recipeUrl,
            String itemUrl
    ) {
        String ingredientJson = "{";
        ingredientJson = addCustomProperty(
                ingredientJson,
                "id", ingredient.getId() + "",
                true
        );
        ingredientJson = addCustomProperty(
                ingredientJson,
                "version", ingredient.getVersion() + "",
                true
        );
        ingredientJson = addCustomProperty(
                ingredientJson,
                "condition", ingredient.getCondition() + "",
                true
        );
        ingredientJson = addCustomProperty(
                ingredientJson,
                "quantity", ingredient.getQuantity() + "",
                true
        );
        ingredientJson = addCustomProperty(
                ingredientJson,
                "item",
                itemUrl,
                true
        );
        ingredientJson = addCustomProperty(
                ingredientJson,
                "recipe",
                recipeUrl,
                false // no comma at the end
        );
        ingredientJson += "}";
        return ingredientJson;
    }

    // Tests checking that all DAOs are good, i.e.
    // no referential integrity violations

    @Test
    public void ingredientsRestPageCanBeRendered()
            throws Exception {
        mockMvc.perform(
                get(BASE_URL + INGREDIENTS_REST_PAGE)
        ).andDo(print())
                .andExpect(
                        status().isOk()
                );
    }


    // POST items tests



    // Ingredient POST tests


    @Test
    public void postEmptyJsonToCreateNewIngredientShouldBeBadRequestWithValidationErrors()
            throws Exception {
        // Arrange : mockMvc created with webAppContext

        // Arrange : calculate number of ingredients before req
        int numberOfIngredientsBeforeReq =
                getSizeOfIterable(
                        ingredientDao.findAll()
                );

        // Act and Assert:
        // When POST request to INGREDIENTS_REST_PAGE is
        // made with empty JSON "{}"
        // Then :
        // - status should be "bad request"
        // - 7 errors should be thrown
        // - @NotNull, @ValidItem for "item" field
        //   @NotNull, @NotEmpty for "condition" field
        //   @NotNull, @NotEmpty for "quantity" field
        //   @NotNull for "recipe" field
        mockMvc.perform(
                post(BASE_URL + INGREDIENTS_REST_PAGE)
                        .contentType(contentType)
                        .content("{}")
        ).andDo(print())
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.errors",
                                hasSize(7)
                        )
                );

        // Assert that number of ingredients stayed same
        assertThat(
                getSizeOfIterable(
                        ingredientDao.findAll()
                ),
                is(numberOfIngredientsBeforeReq)
        );
    }

    @Test
    public void postNewIngredientWithValidFieldsMadeByIngredientRecipeOwnerShouldCreateNewIngredient()
            throws Exception {
        // Arrange : mockMvc created with webAppContext

        // Arrange : calculate number of ingredients before req
        int numberOfIngredientsBeforeReq =
                getSizeOfIterable(
                        ingredientDao.findAll()
                );

        // Arrange : create test ingredient to be added
        // we put "null" for item, because we manually
        // specify its url later on
        Ingredient testIngredient =
                new Ingredient(null, "condition", "quantity");

        // Arrange: get first recipe
        Recipe firstRecipe = recipeDao.findOne(1L);

        // Arrange: get logged user that is non-admin, but
        // owner
        User ingredientRecipeOwner =
                (User) userService.loadUserByUsername("jd");
        assertThat(
                "user is ingredient.recipe.owner",
                ingredientRecipeOwner,
                is(
                        firstRecipe.getOwner()
                )
        );
        assertThat(
                "user is ingredient.recipe.owner",
                ingredientRecipeOwner.getRole(),
                hasProperty(
                        "name", equalTo("ROLE_USER")
                )
        );

        // Act and Assert:
        // When POST request to INGREDIENTS_REST_PAGE is
        // made with valid JSON:
        // with 1-st "item" and 1-st "recipe"
        // Then :
        // - status should be "created"
        mockMvc.perform(
                post(BASE_URL + INGREDIENTS_REST_PAGE)
                        .contentType(contentType)
                        .content(
                                generateIngredientJsonWithItemAndRecipe(
                                        testIngredient,
                                        BASE_URL + RECIPES_REST_PAGE + "/1",
                                        BASE_URL + ITEMS_REST_PAGE + "/1"
                                )
                        )
                        .with(
                                SecurityMockMvcRequestPostProcessors.user(
                                        ingredientRecipeOwner
                                )
                        )
        ).andDo(print())
                .andExpect(
                        status().isCreated()
                );

        // Assert that number of ingredients increased
        assertThat(
                getSizeOfIterable(
                        ingredientDao.findAll()
                ),
                is(numberOfIngredientsBeforeReq + 1)
        );
    }
}