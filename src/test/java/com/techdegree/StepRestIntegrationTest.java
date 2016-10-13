package com.techdegree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techdegree.dao.StepDao;
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

import static com.techdegree.testing_shared_helpers.GenericJsonWithLinkGenerator.addCustomProperty;
import static com.techdegree.testing_shared_helpers.IterablesConverterHelper.getSizeOfIterable;
import static com.techdegree.web.WebConstants.STEPS_REST_PAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:./test-StepRestIntegrationTest.properties")
public class StepRestIntegrationTest {

    // will be something like "/api/v1":
    // is set from properties file
    @Value("${spring.data.rest.base-path}")
    private String BASE_URL;

    private JacksonTester<Step> stepJacksonTester;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // we autowire DAO-s here because
    // REST is created through DAOs, not
    // services
    @Autowired
    private StepDao stepDao;

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

    // Tests checking that all DAOs are good, i.e.
    // no referential integrity violations

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

    // POST requests tests

    @Test
    public void postNewStepWithValidFieldsShouldCreateNewRecipeStep()
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
    public void postEmptyJsonToCreateNewStepShouldReturnValidationErrors()
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

}
