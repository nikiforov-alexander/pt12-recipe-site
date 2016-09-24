package com.techdegree.service;

import com.techdegree.dao.StepDao;
import com.techdegree.model.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StepServiceImpl implements StepService{
    @Autowired
    private StepDao stepDao;

    @Override
    public Step save(Step step) {
        return stepDao.save(step);
    }

    @Override
    public Step findOne(Long id) {
        return stepDao.findOne(id);
    }

    @Override
    public List<Step> findAll() {
        return (List<Step>) stepDao.findAll();
    }
}
