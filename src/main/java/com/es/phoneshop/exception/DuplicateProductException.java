package com.es.phoneshop.exception;

public class DuplicateProductException extends RuntimeException{
    public DuplicateProductException(String message) {
        super(message);
    }

    public DuplicateProductException() {
        super();
    }
}
