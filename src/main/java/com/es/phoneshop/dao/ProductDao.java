package com.es.phoneshop.dao;

import com.es.phoneshop.enam.search.AcceptanceCriteria;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.enam.sort.SortField;
import com.es.phoneshop.enam.sort.SortOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductDao extends GenericDao<Product>{
    List<Product> findProducts(String query, SortField sortField, SortOrder SortOrder, BigDecimal minPrice,
                               BigDecimal maxPrice, AcceptanceCriteria criteria);
    void updateProductStock(UUID productId, int stock);
}