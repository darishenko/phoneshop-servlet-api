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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DefaultOrderService implements OrderService {
    private static volatile OrderService instance;
    private final ProductDao productDao = ArrayListProductDao.getInstance();
    private final OrderDao orderDao = ArrayListOrderDao.getInstance();

    public static OrderService getInstance() {
        if (instance == null) {
            synchronized (DefaultOrderService.class) {
                if (instance == null) {
                    instance = new DefaultOrderService();
                }
            }
        }
        return instance;
    }

    @Override
    public Order createOrder(Cart cart, Locale locale) {
        return new Order(locale, cart.clone(), calculateDeliveryCoast());
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return Arrays.asList(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        orderDao.save(order);
        updateProductsQuantityAfterPlaceOrder(order);
    }

    private void updateProductsQuantityAfterPlaceOrder(Order order) {
        List<CartItem> products = order.getItems();
        products.forEach(cartItem -> {
            Product product = cartItem.getProduct();
            int newStock = product.getStock() - cartItem.getQuantity();
            productDao.updateProductStock(product.getId(), newStock);
        });
    }

    private BigDecimal calculateDeliveryCoast() {
        return new BigDecimal(5);
    }
}
