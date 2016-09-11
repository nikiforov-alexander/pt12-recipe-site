package com.techdegree.dao;

import com.techdegree.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(exported = false)
public interface UserDao extends CrudRepository<User, Long> {
    // "query" method of Spring data: finds user by username
    User findByUsername(String username);
}
