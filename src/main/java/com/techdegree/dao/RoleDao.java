package com.techdegree.dao;

import com.techdegree.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(exported = false)
public interface RoleDao extends CrudRepository<Role, Long> {

}
