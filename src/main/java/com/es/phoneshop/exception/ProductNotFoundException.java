package com.es.phoneshop.exception;

public class ProductNotFoundException extends RuntimeException {
    private long productId;

    public ProductNotFoundException() {
        super();
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(long productId) {
        super();
        this.productId = productId;
    }

    public long getProductId() {
        return productId;
    }

}
