package com.es.phoneshop.exception;

public class AddExistingProductException extends RuntimeException{
    public AddExistingProductException(String message) {
        super(message);
    }

    public AddExistingProductException() {
        super();
    }
}
