package com.es.phoneshop.model;

import java.util.UUID;

public abstract class Item {
    protected UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Item(){
        id = UUID.randomUUID();
    }

}
