package com.es.phoneshop.model.product.comparator;

import com.es.phoneshop.model.product.Product;

import java.util.Comparator;

public class ProductDescriptionWordsCountComparator implements Comparator<Product> {

    @Override
    public int compare(Product product1, Product product2) {
        return Integer.compare(getProductDescriptionWordsCount(product1), getProductDescriptionWordsCount(product2));
    }

    private int getProductDescriptionWordsCount(Product product) {
        return product.getDescription().split("\\s+").length;
    }
}
