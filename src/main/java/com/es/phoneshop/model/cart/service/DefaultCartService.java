package com.es.phoneshop.model.cart.service;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.ProductDao;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private static volatile CartService instance;
    private final ProductDao productDao;

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static CartService getInstance() {
        if (instance == null) {
            synchronized (DefaultCartService.class) {
                if (instance == null) {
                    instance = new DefaultCartService();
                }
            }
        }
        return instance;
    }

    @Override
    public Cart getCart(HttpSession currentSession) {
        synchronized (currentSession.getId().intern()) {
            Cart cart = (Cart) currentSession.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                currentSession.setAttribute(CART_SESSION_ATTRIBUTE, cart);
            }
            return cart;
        }
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        Product product = productDao.getProduct(productId);
        synchronized (cart) {
            CartItem cartItem = cart.getItems()
                    .stream()
                    .filter(item -> productId.equals(item.getProduct().getId()))
                    .findAny()
                    .orElse(new CartItem(product, 0));
            int availableProductQuantity = product.getStock() - cartItem.getQuantity();
            if (availableProductQuantity >= quantity) {
                addProductToCart(cart, cartItem, quantity);
            } else {
                throw new OutOfStockException(product, quantity, availableProductQuantity);
            }
        }
    }

    private void addProductToCart(Cart cart, CartItem cartItem, int quantity) {
        if (cartItem.getQuantity() == 0) {
            cart.getItems().add(cartItem);
        }
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }

}
