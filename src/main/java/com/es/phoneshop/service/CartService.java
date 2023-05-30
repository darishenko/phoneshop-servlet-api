package com.es.phoneshop.service;

import com.es.phoneshop.model.cart.Cart;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

public interface CartService {
    Cart getCart(HttpSession currentSession);
    void add(Cart cart, UUID productId, int quantity);
    void update(Cart cart, UUID productId, int quantity);
    void delete(Cart cart, UUID productId);
    void clearCart(HttpSession currentSession);
}
