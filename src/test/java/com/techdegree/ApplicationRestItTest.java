package com.techdegree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techdegree.dao.IngredientDao;
import com.techdegree.dao.ItemDao;
import com.techdegree.dao.RecipeDao;
import com.techdegree.dao.StepDao;
import com.techdegree.model.Ingredient;
import com.techdegree.model.Item;
import com.techdegree.model.Recipe;
import com.techdegree.model.Step;
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

import java.util.List;

import static com.techdegree.web.WebConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:./test-ApplicationRestItTest.properties")
public class ApplicationRestItTest {

    // will be something like "/api/v1":
    // is set from properties file
    @Value("${spring.data.rest.base-path}")
    private String BASE_URL;

    private JacksonTester<Recipe> json;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // we autowire DAO-s here because
    // REST is created through DAOs, not
    // services
    @Autowired
    private RecipeDao recipeDao;
    @Autowired
    private StepDao stepDao;
    @Autowired
    private IngredientDao ingredientDao;
    @Autowired
    private ItemDao itemDao;

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
     * Adds custom property with {@code name} and
     * {@code value} to passed {@code json}, with comma
     * or without it depending on {@code withComma}.
     * @param json : JSON string to which parameter will
     *             be added
     * @param name : name of property
     * @param value : value of property
     * @param withComma : adds comma at the ends, if true,
     *                  nothing - otherwise
     * @return String {@code json} plus property and/or comma:
     * {@code "name":"value"}
     * or
     * {@code "name":"value",}
     */
    private String addCustomProperty(
            String json,
            String name,
            String value,
            boolean withComma
    ) {
        if (withComma) {
            return json +
                    "\"" + name +
                    "\":\"" + value + "\",";
        } else {
            return json +
                    "\"" + name +
                    "\":\"" + value + "\"";
        }
    }

    /**
     * Generates String JSON of {@code Step} with
     * @param step Step, which values will be set
     *             to JSON properties
     * @param recipeUrl String, recipe url that is
     *                  added to the JSON in order
     *                  for steps to be created with
     *                  some recipe
     * @return String JSON of Step:
     * for example:
     * @{code
     *  {
     *      "id" : "null",
     *      "version" : "null",
     *      "description" : "description"
     *      "recipe" : "/api/v1/recipes/1"
     *  }
     * }
     */
    private String generateStepJsonWithFirstRecipe(
            Step step,
            String recipeUrl
    ) {
        String stepJson = "{";
        stepJson = addCustomProperty(
                stepJson,
                "id", step.getId() + "",
                true
        );
        stepJson = addCustomProperty(
                stepJson,
                "version", step.getVersion() + "",
                true
        );
        stepJson = addCustomProperty(
                stepJson,
                "description",
                step.getDescription(),
                true
        );
        stepJson = addCustomProperty(
                stepJson,
                "recipe",
                recipeUrl,
                false // no comma at the end
        );
        stepJson += "}";
        return stepJson;
    }

    /**
     * Generates String JSON of {@code Item} with
     * @param item Item, which values will be set
     *             to JSON properties
     * @return String JSON of Item:
     * for example:
     * @{code
     *  {
     *      "id" : "null",
     *      "version" : "null",
     *      "name" : "name"
     *  }
     * }
     */
    private String generateItemJson(
            Item item
    ) {
        String itemJson = "{";
        itemJson = addCustomProperty(
                itemJson,
                "id", item.getId() + "",
                true
        );
        itemJson = addCustomProperty(
                itemJson,
                "version", item.getVersion() + "",
                true
        );
        itemJson = addCustomProperty(
                itemJson,
                "name",
                item.getName(),
                false // no comma at the end
        );
        itemJson += "}";
        return itemJson;
    }

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


    /**
     * returns size of iterable
     * @param iterable
     * @return size of passed ${@code iterable}
     */
    private int getSizeOfIterable(Iterable<?> iterable) {
        List<?> list = (List<?>) iterable;
        return list.size();
    }

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

    @Test
    public void ingredientsPageCanBeRendered()
            throws Exception {
        mockMvc.perform(
                get(BASE_URL + INGREDIENTS_REST_PAGE)
        ).andDo(print())
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    public void stepsPageCanBeRendered()
            throws Exception {
        mockMvc.perform(
                get(BASE_URL + STEPS_REST_PAGE)
        ).andDo(print())
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    public void itemsPageCanBeRendered()
            throws Exception {
        mockMvc.perform(
                get(BASE_URL + ITEMS_REST_PAGE)
        ).andDo(print())
                .andExpect(
                        status().isOk()
                );
    }

    // POST requests tests: steps

    @Test
    public void postRequestWithValidStepFieldsCanCreateNewRecipeStep()
            throws Exception {
        // Arrange: mockMvc with real app context

        // Arrange : generate testStep to be added
        // using POST request
        Step testStep = new Step("test description");

        // Arrange: calculate number of steps before req
        int numberOfStepsBeforePostReq =
                getSizeOfIterable(
                        stepDao.findAll()
                );

        // Act and Assert:
        // When POST request to STEPS_REST_PAGE is made
        // with "testStep" converted to JSON and
        // recipe with "id=1"
        // Then:
        // - status should be created
        mockMvc.perform(
                post(
                        BASE_URL + STEPS_REST_PAGE
                ).contentType(contentType)
                .content(
                        generateStepJsonWithFirstRecipe(
                                testStep,
                                BASE_URL + "/recipes/1"
                        )
                )
        ).andDo(print())
        .andExpect(
                status().isCreated()
        );

        // Assert that new step will be found in stepDao
        assertThat(
                getSizeOfIterable(
                        stepDao.findAll()
                ),
                is(
                        numberOfStepsBeforePostReq + 1
                )
        );

        // Assert that number of steps for recipe increased
        // TODO: find out why lazy instantiation problem is thrown
        // assertThat(
        //        recipeDao.findOne(1L).getSteps().size(),
        //        is(
        //                numberOfRecipeStepsBeforePostReq + 1
        //        )
        // );
    }

    @Test
    public void postRequestWithInvalidStepFieldsReturnsValidationErrors()
            throws Exception {
        // Arrange: mockMvc with real app context

        // Arrange: calculate number of steps before req
        int numberOfStepsBeforePostReq =
                getSizeOfIterable(
                        stepDao.findAll()
                );

        // Act and Assert:
        // When POST request to STEPS_REST_PAGE is made
        // with empty JSON
        // Then:
        // - status should bad request
        // - 3 errors are expected :
        //  @NotNull, @NotEmpty for step.description
        //  @NotNull for step.recipe
        mockMvc.perform(
                post(
                        BASE_URL + STEPS_REST_PAGE
                ).contentType(contentType)
                .content(
                        "{}"
                )
        ).andDo(print())
        .andExpect(
                status().isBadRequest()
        )
        .andExpect(
                jsonPath(
                        "$.errors",
                        hasSize(3)
                )
        );

        // Assert that number of steps is the same
        assertThat(
                getSizeOfIterable(
                        stepDao.findAll()
                ),
                is(
                        numberOfStepsBeforePostReq
                )
        );
    }

    // POST items tests

    @Test
    public void postWithEmptyJsonToCreateNewItemReturnsValidationErrors()
            throws Exception {
        // Arrange : mockMvc is set up with webAppContext

        // Arrange : calculate number of items before req
        int numberOfItemsBeforeReq =
                getSizeOfIterable(
                        itemDao.findAll()
                );

        // Act and Assert:
        // When POST request to ITEMS_REST_PAGE is
        // made with empty JSON "{}"
        // Then :
        // - status should be Bad Request
        // - there has to be two errors
        //   @NotNull and @NotEmpty for "item.name"
        mockMvc.perform(
                post(
                        BASE_URL + ITEMS_REST_PAGE
                ).contentType(contentType)
                        .content("{}")
        ).andDo(print())
                .andExpect(
                        status().isBadRequest()
                )
                .andExpect(
                        jsonPath(
                                "$.errors",
                                hasSize(2)
                        )
                );

        // Assert that no items should be created
        assertThat(
                getSizeOfIterable(
                        itemDao.findAll()
                ),
                is(
                        numberOfItemsBeforeReq
                )
        );
    }

    @Test
    public void postWithValidItemCreatesNewItem()
            throws Exception {
        // Arrange : mockMvc is set up with webAppContext

        // Arrange : calculate number of items before req
        int numberOfItemsBeforeReq =
                getSizeOfIterable(
                        itemDao.findAll()
                );

        // Arrange testItem
        Item testItem = new Item("test item name");

        // Act and Assert:
        // When POST request to ITEMS_REST_PAGE is
        // made with valid Item JSON
        // Then :
        // - status should be "created"
        mockMvc.perform(
                post(
                        BASE_URL + ITEMS_REST_PAGE
                ).contentType(contentType)
                .content(
                        generateItemJson(testItem)
                )
        ).andDo(print())
        .andExpect(
                status().isCreated()
        );

        // Assert number of items increased
        assertThat(
                getSizeOfIterable(
                        itemDao.findAll()
                ),
                is(
                        numberOfItemsBeforeReq + 1
                )
        );
    }

    // Ingredient POST tests


    @Test
    public void postCreatingNewIngredientWithEmptyJsonShouldBeBadRequestWithValidationErrors()
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
    public void postRequestWithValidIngredientFieldShouldCreateNewIngredient()
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