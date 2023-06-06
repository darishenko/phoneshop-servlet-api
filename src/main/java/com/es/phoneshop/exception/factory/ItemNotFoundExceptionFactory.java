package com.es.phoneshop.exception.factory;

import com.es.phoneshop.dao.impl.ArrayListOrderDao;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.exception.order.OrderNotFoundException;
import com.es.phoneshop.exception.product.ProductNotFoundException;
import com.es.phoneshop.model.Item;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.product.Product;

import java.util.UUID;

public class ItemNotFoundExceptionFactory {
    private static volatile ItemNotFoundExceptionFactory instance;

    public static ItemNotFoundExceptionFactory getInstance() {
        if (instance == null) {
            synchronized (ArrayListOrderDao.class) {
                if (instance == null) {
                    instance = new ItemNotFoundExceptionFactory();
                }
            }
        }
        return instance;
    }

    public ItemNotFoundException createItemNotFoundException(Class<? extends Item> itemClass, UUID itemId) {
        if (itemClass.equals(Product.class)) {
            return new ProductNotFoundException(itemId);
        }
        if (itemClass.equals(Order.class)) {
            return new OrderNotFoundException(itemId);
        }

        throw new IllegalArgumentException("Unsupported item class: " + itemClass.getSimpleName());
    }

}
