package com.es.phoneshop.exception.product;

public class DuplicateProductException extends RuntimeException{
    public DuplicateProductException(String message) {
        super(message);
    }

    public DuplicateProductException() {
        super();
    }
}
