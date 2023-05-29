package com.es.phoneshop.model.cart.service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class DefaultQuantityService implements QuantityService {
    private static volatile QuantityService instance;

    private DefaultQuantityService() {

    }

    public static QuantityService getInstance() {
        if (instance == null) {
            synchronized (DefaultCartService.class) {
                if (instance == null) {
                    instance = new DefaultQuantityService();
                }
            }
        }
        return instance;
    }

    @Override
    public int parseQuantity(String quantity, Locale locale) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        Number quantityNumber = numberFormat.parse(quantity);

        int quantityIntValue = quantityNumber.intValue();
        if (quantityNumber.doubleValue() != quantityNumber.intValue()) {
            throw new NumberFormatException("Quantity must be an integer");
        }
        if (quantityIntValue <= 0) {
            throw new NumberFormatException("Quantity must be a positive number");
        }
        return quantityIntValue;
    }

}
