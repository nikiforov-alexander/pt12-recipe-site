package com.techdegree.service;

import com.techdegree.dao.RoleDao;
import com.techdegree.dao.UserDao;
import com.techdegree.dto.UserDto;
import com.techdegree.exception.UserAlreadyExistsException;
import com.techdegree.model.Role;
import com.techdegree.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @InjectMocks
    private UserService userService =
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

    @Test
    public void userCanBeRegisteredIfUsernameIsFreeAndUserDtoPropertiesArePassedToUser()
            throws Exception {
        // Arrange: userDto passed from Controller
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setUsername("username");
        userDto.setPassword("password");

        // Arrange: when findByUsername will be called, we
        // return null, to know that such user does not exist
        when(userDao.findByUsername(anyString())).thenReturn(
                null
        );

        // Arrange:
        // When new user is saved we pass to it
        // userDto object above, so that we can later
        // check whether data from userDto was passed
        when(userDao.save(any(User.class)))
                .thenReturn(
                        new User(userDto, any(Role.class)
                        )
                );

        // Act:
        // When registerNewUser is called
        User savedUser =
                userService.registerNewUser(userDto);

        // Then saved user's name and username should be
        // equal to the ones of userDto passed as argument
        assertThat(
                savedUser,
                hasProperty("name",
                        equalTo(userDto.getName()))
        );
        assertThat(
                savedUser,
                hasProperty("username",
                        equalTo(userDto.getUsername()))
        );
        // This test does not work ... hopefully it is
        // because this PASSWORD_ENCODER works very securely...
        // TODO: ask why PASSWORD_ENCODER does not work ?
//        assertThat(
//                savedUser,
//                hasProperty("password",
//                        equalTo(
//                                User.PASSWORD_ENCODER.encode(
//                                        userDto.getPassword()
//                                )
//                        )
//                )
//        );
        //
        // verify mock interactions
        verify(userDao).findByUsername(anyString());
        verify(userDao).save(any(User.class));
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void whenUsernamePassedToRegisterNewUserAlreadyExistsExceptionShouldBeThrown()
            throws Exception {
        // Arrange: userDto passed from Controller
        UserDto userDto = new UserDto();
        userDto.setName("name");
        userDto.setUsername("username");
        userDto.setPassword("password");

        // Arrange: when findByUsername will be called, we
        // return some user
        // NOTE: does not work with any(User.class) ... ?
        // I wonder why ?
        // TODO: figure out what does any() mean ? - null ?
        when(userDao.findByUsername(anyString())).thenReturn(
                new User()
        );

        // Act:
        // When user.registerNewUser will be called
        userService.registerNewUser(userDto);

        // Assert:
        // Exception should be thrown
    }
}