package com.techdegree.service;

import com.techdegree.dao.OwnerDao;
import com.techdegree.model.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerServiceImpl implements OwnerService {
    @Autowired
    private OwnerDao ownerDao;

    @Override
    public List<Owner> findAll() {
        return (List<Owner>) ownerDao.findAll();
    }

    @Override
    public Owner save(Owner owner) {
        return ownerDao.save(owner);
    }
}
