package com.es.phoneshop.service;

import com.es.phoneshop.enam.order.PaymentMethod;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;

import java.util.List;
import java.util.Locale;

public interface OrderService {
    Order createOrder(Cart cart, Locale locale);

    List<PaymentMethod> getPaymentMethods();

    void placeOrder(Order order);

}
