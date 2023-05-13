package com.es.phoneshop.model.product;

import java.util.ArrayDeque;
import java.util.Deque;

public class RecentProducts {
    private static final int RECENT_PRODUCTS_MAX_COUNT = 3;
    private Deque<Product> products = new ArrayDeque<>();

    public Deque<Product> getProducts() {
        return products;
    }

    public void setProducts(Deque<Product> products) {
        this.products = products;
    }

}
