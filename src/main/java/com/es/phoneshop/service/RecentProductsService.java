package com.es.phoneshop.service;

import com.es.phoneshop.model.product.RecentProducts;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

public interface RecentProductsService {
    RecentProducts getRecentProducts(HttpSession currentSession);
    void addToRecentProducts(RecentProducts recentProducts, UUID productId);
}
