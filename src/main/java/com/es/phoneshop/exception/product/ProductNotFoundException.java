package com.es.phoneshop.exception.product;

import com.es.phoneshop.exception.ItemNotFoundException;

import java.util.UUID;

public class ProductNotFoundException extends ItemNotFoundException {
    public ProductNotFoundException(){
        super();
    }

    public ProductNotFoundException(UUID productId){
        super(productId);
    }
}
