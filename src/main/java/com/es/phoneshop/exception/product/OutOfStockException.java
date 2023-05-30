package com.es.phoneshop.exception.product;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends RuntimeException {
    private final Product product;
    private final int requestedQuantity;
    private final int availableQuantity;

    public OutOfStockException(Product product, int requestedQuantity, int availableQuantity) {
        this.product = product;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
