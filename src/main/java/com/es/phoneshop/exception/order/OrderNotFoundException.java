package com.es.phoneshop.exception.order;

import com.es.phoneshop.exception.ItemNotFoundException;

import java.util.UUID;

public class OrderNotFoundException extends ItemNotFoundException {
    public OrderNotFoundException() {
        super();
    }

    public OrderNotFoundException(UUID orderId) {
        super(orderId);
    }
}
