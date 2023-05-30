package com.es.phoneshop.exception;

import com.es.phoneshop.model.Item;

import java.util.UUID;

public abstract class ItemNotFoundException extends RuntimeException {
    private UUID itemId;

    public ItemNotFoundException() {
        super();
    }

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(UUID orderId) {
        super();
        this.itemId = orderId;
    }

    public UUID getItemId() {
        return itemId;
    }
}
