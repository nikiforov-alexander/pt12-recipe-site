package com.techdegree.dao;

import com.techdegree.model.User;
import com.techdegree.service.CustomUserDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
// Wanted to use DataJpaTest, but could not
// so lets live as
// TODO: figure out how to use DataJpaTest
//@DataJpaTest
@TestPropertySource("classpath:./test-RecipeDaoTest.properties")
public class RecipeDaoTest {
    @Autowired
    private RecipeDao recipeDao;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private CustomUserDetailsService userService;

    @Before
    public void setUp() throws Exception {
        webAppContextSetup(webAppContext).build();
    }

    @Test
    public void findAllFavoritesReturnsOneFavoriteRecipeWithDataLoader()
            throws Exception {
        // Arrange: get User "jd" from UserService that
        // is loaded in DataLoader class as owner of recipe,
        // and person with favorite recipe
        User user =  (User) userService.loadUserByUsername("jd");

        // Act and Assert:
        // When find all favorite recipes method is called
        // one favorite recipe for "jd" user is returned
        assertThat(
                recipeDao.findAllFavoriteRecipesFor(
                        user
                ),
                hasSize(1)
        );
    }
}
