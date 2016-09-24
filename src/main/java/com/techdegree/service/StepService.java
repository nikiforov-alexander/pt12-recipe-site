package com.techdegree.service;

import com.techdegree.model.Step;

import java.util.List;

public interface StepService {
    Step save(Step step);
    Step findOne(Long id);
    List<Step> findAll();
}
