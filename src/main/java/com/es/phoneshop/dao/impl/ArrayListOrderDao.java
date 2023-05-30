package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.order.Order;

public class ArrayListOrderDao extends ArrayListGenericDao<Order> implements OrderDao {
    private static volatile OrderDao instance;

    private ArrayListOrderDao() {
        super(Order.class);
    }

    public static OrderDao getInstance() {
        if (instance == null) {
            synchronized (ArrayListOrderDao.class) {
                if (instance == null) {
                    instance = new ArrayListOrderDao();
                }
            }
        }
        return instance;
    }

}
