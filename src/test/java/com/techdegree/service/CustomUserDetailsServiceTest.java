package com.techdegree.service;

import com.techdegree.dao.RoleDao;
import com.techdegree.dao.UserDao;
import com.techdegree.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @InjectMocks
    private CustomUserDetailsService userService =
        new CustomUserDetailsService();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void loadUserByUsernameReturnsUser() throws Exception {
        // Arrange: test user
        User testUser = new User();
        // Arrange:
        // When findByUserName is called, test User will be
        // returned
        when(userDao.findByUsername(anyString()))
                .thenReturn(testUser);

        // Act and Assert:
        // when loadByUsername is called
        User foundUser =
                (User) userService.loadUserByUsername(anyString());

        // then returned user casted to (main.package) user
        // should be returned
        assertThat(
                foundUser,
                is(testUser)
        );
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameThrowsExceptionWhenUserNotFound()
            throws Exception {
        // Arrange:
        // When findByUserName returns null
        when(userDao.findByUsername(anyString()))
            .thenReturn(null);

        // Act:
        // when loadByUsername is called
        userService.loadUserByUsername(anyString());

        // Assert:
        // Then UsernameNotFoundException should be
        // thrown
    }
}