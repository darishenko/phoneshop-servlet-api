package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.cart.service.DefaultQuantityService;
import com.es.phoneshop.model.cart.service.QuantityService;
import org.junit.Before;
import org.junit.Test;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DefaultQuantityServiceTest {
    Locale locale;
    QuantityService quantityService = DefaultQuantityService.getInstance();

    @Before
    public void setup() {
        locale = new Locale("ru");
    }

    @Test(expected = NumberFormatException.class)
    public void parseQuantity_zeroQuantity_NumberFormatException() throws ParseException {
        String quantity = "0";

        quantityService.parseQuantity(quantity, locale);
    }

    @Test(expected = NumberFormatException.class)
    public void parseQuantity_notIntegerQuantity_NumberFormatException() throws ParseException {
        String quantity = "1,2";

        quantityService.parseQuantity(quantity, locale);
    }

    @Test
    public void parseQuantity_validQuantity_returnQuantityValue() throws ParseException {
        int quantityValue;
        String quantity = "1.0";
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        quantityValue = quantityService.parseQuantity(quantity, locale);

        assertEquals(quantityValue, numberFormat.parse(quantity).intValue());
    }
}
