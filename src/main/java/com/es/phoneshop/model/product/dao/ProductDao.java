package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.sortEnum.SortField;
import com.es.phoneshop.model.product.sortEnum.SortOrder;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id);
    List<Product> findProducts(String query, SortField sortField, SortOrder SortOrder);
    void save(Product product);
    void delete(Long id);
}