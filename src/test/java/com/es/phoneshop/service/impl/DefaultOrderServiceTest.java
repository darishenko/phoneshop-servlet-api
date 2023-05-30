package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListOrderDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.enam.order.PaymentMethod;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultOrderServiceTest {
    private OrderService orderService;
    @Mock
    private OrderDao orderDao = Mockito.mock(ArrayListOrderDao.class);
    @Mock
    private ProductDao productDao = Mockito.mock(ArrayListProductDao.class);

    @Before
    public void init() throws NoSuchFieldException, IllegalAccessException {
        orderService = DefaultOrderService.getInstance();
        setOrderDao();
        setProductDao();
    }

    @Test
    public void getOrder_returnOrder() {
        Cart cart = new Cart();

        Order order = orderService.createOrder(cart, new Locale("ru"));

        assertNotNull(order);
        assertEquals(cart.getItems().size(), order.getItems().size());
        assertEquals(cart.getTotalCost(), order.getSubTotal());
    }

    @Test
    public void getPaymentMethods_returnPaymentMethods() {
        List<PaymentMethod> paymentMethods = Arrays.stream(PaymentMethod.values()).toList();

        List<PaymentMethod> methods = orderService.getPaymentMethods();

        assertEquals(paymentMethods, methods);
    }

    @Test
    public void placeOrder_newOrder() {
        int productStock = 2;
        Order order = createOrder(productStock);

        orderService.placeOrder(order);

        verify(orderDao).save(order);
        verify(productDao).updateProductStock(any(), eq(1));
    }

    private Order createOrder(int productStock){
        Order order = Mockito.mock(Order.class);
        Product product = Mockito.mock(Product.class);
        when(product.getStock()).thenReturn(productStock);
        CartItem cartItem = new CartItem(product, productStock -1);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);
        when(order.getItems()).thenReturn(items);
        return order;
    }

    private void setOrderDao() throws IllegalAccessException, NoSuchFieldException {
        Field orderDaoField = DefaultOrderService.class.getDeclaredField("orderDao");
        orderDaoField.setAccessible(true);
        orderDaoField.set(orderService, orderDao);
    }

    private void setProductDao() throws IllegalAccessException, NoSuchFieldException {
        Field productrDaoField = DefaultOrderService.class.getDeclaredField("productDao");
        productrDaoField.setAccessible(true);
        productrDaoField.set(orderService, productDao);
    }

}
