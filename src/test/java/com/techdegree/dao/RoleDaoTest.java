package com.techdegree.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url = jdbc:h2:./database/test-RoleDaoTest-recipes;DB_CLOSE_ON_EXIT=FALSE"
)
public class RoleDaoTest {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        webAppContextSetup(webApplicationContext).build();
    }

    // this tests ensures that we have two roles when we use
    // DataLoader class, and when we register new user, we
    // set his role to the first one
    @Test
    public void findByRoleReturnsTwoRolesWithDataLoader() throws Exception {
        assertThat(
                roleDao.findOne(1L),
                hasProperty("name", equalTo("ROLE_USER"))
        );
        assertThat(
                roleDao.findOne(2L),
                hasProperty("name", equalTo("ROLE_ADMIN"))
        );
    }
}