package com.techdegree.service;

import com.techdegree.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> findAll();
    Item findOne(Long id);
    Item save(Item item);
    void delete(Item item);
}
