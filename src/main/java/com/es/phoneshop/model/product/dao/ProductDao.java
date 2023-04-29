package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductException;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id) throws ProductException;
    List<Product> findProducts();
    void save(Product product) throws ProductException;
    void delete(Long id) throws ProductException;
}
