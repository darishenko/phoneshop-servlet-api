package com.es.phoneshop.dao;

import java.util.UUID;

public interface GenericDao<Item> {
    Item getItem(UUID itemId);

    void save(Item item);

    void delete(UUID itemId);
}
