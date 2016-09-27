package com.techdegree.service;

import com.techdegree.model.Owner;

import java.util.List;

public interface OwnerService {
    List<Owner> findAll();
    Owner save(Owner owner);
}
