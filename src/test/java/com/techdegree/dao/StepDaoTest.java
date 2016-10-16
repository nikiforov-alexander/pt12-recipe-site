package com.techdegree.dao;

import com.techdegree.model.Recipe;
import com.techdegree.model.Step;
import com.techdegree.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
// TODO : change all test classes, and remove unnecessary test.properties files
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.datasource.url = jdbc:h2:./database/test-StepDaoTest-recipes;DB_CLOSE_ON_EXIT=FALSE"
)
public class StepDaoTest {
    @Autowired
    private StepDao stepDao;

    @Autowired
    private RecipeDao recipeDao;

    @Autowired
    private UserDao userDao;

    /**
     * Login and authorizes user by username.
     * @param username of the User to be logged in
     * @return user User that was logged
     */
    private User loginUserByUsername(String username) {
        User user = userDao.findByUsername(username);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                )
        );
        return user;
    }

    @Test
    public void stepCanBeSavedByOwnerOfRecipeStep() throws Exception {
        // Arrange:
        // login "ad" user that is not owner of test step
        // to be saved
        User loggedUser = loginUserByUsername("jd");

        // Arrange:
        // get test recipe
        Recipe firstRecipe = recipeDao.findOne(1L);

        assertThat(
                "logged user is owner",
            loggedUser,
                is(
                        firstRecipe.getOwner()
                )
        );

        // Arrange: create test step with first recipe to be
        // saved
        Step testStep = new Step("test description");
        testStep.setRecipe(firstRecipe);

        // Act : When test step is saved
        Step savedStep = stepDao.save(testStep);

        // set id and version to testStep passed to DAO
        testStep.setId(savedStep.getId());
        testStep.setVersion(savedStep.getVersion());

        assertThat(
                "savedStep is testStep with new version and id",
                savedStep,
                is(testStep)
        );
    }

}