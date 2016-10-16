package com.techdegree.dao;

import com.techdegree.model.Step;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
public interface StepDao extends CrudRepository<Step, Long> {
    // Step can be saved only if user is admin
    // or step.recipe.owner is currently logged
    // in user
    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or " +
        "#step.recipe.owner == authentication.principal")
    Step save(@Param("step") Step step);
}
