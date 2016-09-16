package com.techdegree.service;

import com.techdegree.dao.ItemDao;
import com.techdegree.model.Item;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;

    @Override
    public List<Item> findAll() {
        return (List<Item>) itemDao.findAll();
    }

    @Override
    public Item findOne(Long id) {
        return itemDao.findOne(id);
    }

    @Override
    public Item save(Item item) {
        return itemDao.save(item);
    }

    @Override
    public void delete(Item item) {
        itemDao.delete(item);
    }
}
