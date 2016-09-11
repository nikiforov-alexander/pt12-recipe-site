package com.techdegree.dao;

import com.techdegree.model.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDao extends CrudRepository<Item, Long> {

}
