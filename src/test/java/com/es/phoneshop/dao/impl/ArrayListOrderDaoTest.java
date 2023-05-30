package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.order.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ArrayListOrderDaoTest {
    private static UUID orderId;
    private static OrderDao orderDao;
    private final Order order = new Order();

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        orderDao = ArrayListOrderDao.getInstance();
        orderId = order.getId();
        setOrdersField();
    }

    @Test
    public void getOrder_existingOrderId_returnOrder() {
        Order foundOrder = orderDao.getItem(orderId);

        assertNotNull(foundOrder);
        assertEquals(order, foundOrder);
    }

    @Test(expected = OrderNotFoundException.class)
    public void getOrder_nonexistentOrderId_OrderNotFoundException() {
        orderDao.getItem(UUID.randomUUID());
    }

    @Test
    public void save_newOrder_addOrderToOrderList() {
        Order newOrder = new Order();

        orderDao.save(newOrder);

        assertNotNull(newOrder.getId());
        assertEquals(newOrder, orderDao.getItem(newOrder.getId()));
    }

    private void setOrdersField() throws NoSuchFieldException, IllegalAccessException {
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        Field ordersField = ArrayListGenericDao.class.getDeclaredField("items");
        ordersField.setAccessible(true);
        ordersField.set(orderDao, orders);
    }
}
