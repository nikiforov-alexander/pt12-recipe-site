package com.techdegree.dao;

import com.techdegree.model.Step;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StepDao extends CrudRepository<Step, Long> {

}
