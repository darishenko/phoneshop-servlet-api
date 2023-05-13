package com.es.phoneshop.model.product.service;

import com.es.phoneshop.model.product.RecentProducts;
import jakarta.servlet.http.HttpSession;

public interface RecentProductsService {
    RecentProducts getRecentProducts(HttpSession currentSession);
    void addToRecentProducts(RecentProducts recentProducts, Long productId);
}
