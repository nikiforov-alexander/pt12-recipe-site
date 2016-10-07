package com.techdegree.service;

import com.techdegree.dao.RoleDao;
import com.techdegree.dao.UserDao;
import com.techdegree.dto.UserDto;
import com.techdegree.exception.UserAlreadyExistsException;
import com.techdegree.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
// marked @ComponentScan so that Intellijidea
// finds beans properly
@ComponentScan
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(
                    username + "was not found"
            );
        }
        // here I return user, because com.teamtreehouse.User
        // implements UserDetails.
        return user;
    }

    /**
     * first we check if user with such username exists, if yes
     * we throw exception: that is handled, for now improperly in
     * LoginController. If username is free we create new user
     * from UserDto passed as argument and ROLE_USER that is
     * first in our roleDao.
     * @param userDto - our validated Data Transfer object from
     *                controller.
     * @throws UserAlreadyExistsException when user already exists
     * with this username
     * @return newly saved User. For now it is used for testing
     * purposes. Although Spring Data save methods are always
     * return user, so may be it is not so bad idea
     */
    public User registerNewUser(UserDto userDto)
            throws UserAlreadyExistsException {
        if (userDao.findByUsername(userDto.getUsername()) != null) {
            throw new UserAlreadyExistsException();
        }
        // I won't even put a protection for findByName, because
        // we MUST have ROLE_USER. In the worst case scenario,
        // it will be null. I tested that roleDao.findOne(1L)
        // returns ROLE_USER in RoleDaoTest
        return userDao.save(
                new User(
                        userDto,
                        roleDao.findOne(1L)
                )
        );
    }

}
