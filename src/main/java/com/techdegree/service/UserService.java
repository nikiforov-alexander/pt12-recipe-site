package com.techdegree.service;

import com.techdegree.dto.UserDto;
import com.techdegree.exception.UserAlreadyExistsException;
import com.techdegree.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
    User registerNewUser(UserDto userDto)
            throws UserAlreadyExistsException;
    List<User> findAll();
}
