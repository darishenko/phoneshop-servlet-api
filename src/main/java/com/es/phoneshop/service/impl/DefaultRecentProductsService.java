package com.es.phoneshop.service.impl;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentProducts;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.service.RecentProductsService;
import jakarta.servlet.http.HttpSession;

import java.util.*;

public class DefaultRecentProductsService implements RecentProductsService {
    private static final String RECENT_PRODUCTS_SESSION_ATTRIBUTE =
            DefaultRecentProductsService.class.getName() + ".recentProducts";
    private static final int RECENT_PRODUCTS_MAX_COUNT = 3;

    private static volatile RecentProductsService instance;
    private final ProductDao productDao;

    private DefaultRecentProductsService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static RecentProductsService getInstance() {
        if (instance == null) {
            synchronized (DefaultCartService.class) {
                if (instance == null) {
                    instance = new DefaultRecentProductsService();
                }
            }
        }
        return instance;
    }

    @Override
    public RecentProducts getRecentProducts(HttpSession currentSession) {
        synchronized (currentSession.getId().intern()) {
            RecentProducts recentProducts = (RecentProducts) currentSession
                    .getAttribute(RECENT_PRODUCTS_SESSION_ATTRIBUTE);
            if (recentProducts == null) {
                recentProducts = new RecentProducts();
                currentSession.setAttribute(RECENT_PRODUCTS_SESSION_ATTRIBUTE, recentProducts);
            }
            return recentProducts;
        }
    }

    @Override
    public void addToRecentProducts(RecentProducts recentProducts, UUID productId) {
        Objects.requireNonNull(productId);

        synchronized (recentProducts) {
            Deque<Product> products = recentProducts.getProducts();
            deleteProductIfPresentInRecentProducts(products, productId);
            if (products.size() >= RECENT_PRODUCTS_MAX_COUNT) {
                products.remove();
            }
            products.addLast(productDao.getItem(productId));
        }
    }

    private void deleteProductIfPresentInRecentProducts(Queue<Product> recentProducts, UUID productId) {
            recentProducts.stream()
                    .filter(currentProduct -> productId.equals(currentProduct.getId()))
                    .findAny()
                    .ifPresent(recentProducts::remove);
    }

}
