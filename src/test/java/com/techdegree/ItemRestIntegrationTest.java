package com.techdegree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techdegree.dao.IngredientDao;
import com.techdegree.dao.ItemDao;
import com.techdegree.model.Item;
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
import static com.techdegree.web.WebConstants.ITEMS_REST_PAGE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url = jdbc:h2:./database/test-ItemRestIntegrationTest-recipes;DB_CLOSE_ON_EXIT=FALSE"
)
public class ItemRestIntegrationTest {

    // will be something like "/api/v1":
    // is set from properties file
    @Value("${spring.data.rest.base-path}")
    private String BASE_URL;

    private JacksonTester<Item> itemJacksonTester;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // we autowire DAO-s here because
    // REST is created through DAOs, not
    // services
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

    // Tests checking that all DAOs are good, i.e.
    // no referential integrity violations

    // POST items tests

    @Test
    public void postEmptyJsonToCreateNewItemReturnsValidationErrors()
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
    public void postNewItemWithValidFieldsShouldCreateNewItem()
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
}
