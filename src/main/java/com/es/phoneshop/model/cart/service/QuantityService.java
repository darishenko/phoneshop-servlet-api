package com.es.phoneshop.model.cart.service;

import java.text.ParseException;
import java.util.Locale;

public interface QuantityService {
    int parseQuantity(String quantity, Locale locale) throws ParseException;
}
