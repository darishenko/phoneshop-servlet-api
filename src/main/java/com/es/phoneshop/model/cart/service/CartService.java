package com.es.phoneshop.model.cart.service;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import jakarta.servlet.http.HttpSession;

public interface CartService {
    Cart getCart(HttpSession currentSession);
    void add(Cart cart, Long productId, int quantity) throws OutOfStockException;
}
