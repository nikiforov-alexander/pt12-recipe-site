package com.techdegree.service;

import com.techdegree.dto.UserDto;
import com.techdegree.exception.UserAlreadyExistsException;
import com.techdegree.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    UserDetails loadUserByUsername(String username);
    User registerNewUser(UserDto userDto)
            throws UserAlreadyExistsException;
    List<User> findAll();
}
