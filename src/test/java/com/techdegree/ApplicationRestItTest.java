package com.techdegree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techdegree.model.Recipe;
import com.techdegree.model.Step;
import com.techdegree.service.IngredientService;
import com.techdegree.service.RecipeService;
import com.techdegree.service.StepService;
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

import static com.techdegree.web.WebConstants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private StepService stepService;

    @Autowired
    private IngredientService ingredientService;

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
    private String generateStepJsonWithFirstRecipe(Step step) {
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
                BASE_URL + "/recipes/1",
                false // no comma at the end
        );
        stepJson += "}";
        return stepJson;
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

    // POST requests tests

    @Test
    public void postRequestWithValidStepFieldsCanCreateNewRecipeStep()
            throws Exception {
        // Arrange: mockMvc with real app context

        // Arrange : generate testStep to be added
        // using POST request
        Step testStep = new Step("test description");

        // Arrange: calculate number of steps before req
        int numberOfStepsBeforePostReq =
                stepService.findAll().size();

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
                                testStep
                        )
                )
        ).andDo(print())
        .andExpect(
                status().isCreated()
        );

        // Assert that new step will be found in stepService
        assertThat(
                stepService.findAll().size(),
                is(
                        numberOfStepsBeforePostReq + 1
                )
        );

        // Assert that number of steps for recipe increased
        // TODO: find out why lazy instantiation problem is thrown
        // assertThat(
        //        recipeService.findOne(1L).getSteps().size(),
        //        is(
        //                numberOfRecipeStepsBeforePostReq + 1
        //        )
        // );
    }
}