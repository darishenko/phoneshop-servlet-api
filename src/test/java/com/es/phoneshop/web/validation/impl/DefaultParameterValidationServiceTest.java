package com.es.phoneshop.web.validation.impl;

import com.es.phoneshop.enam.order.PaymentMethod;
import com.es.phoneshop.web.validation.ParameterValidationService;
import org.junit.Before;
import org.junit.Test;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.junit.Assert.*;

public class DefaultParameterValidationServiceTest {
    Locale locale = new Locale("ru");
    private ParameterValidationService parameterValidationService;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        parameterValidationService = DefaultParameterValidationService.getInstance();
    }

    @Test
    public void isValidPhone_validPhone_returnTrue() {
        String phone = "123456789";

        boolean isValidPhone = parameterValidationService.isValidPhone(phone);

        assertTrue(isValidPhone);
    }

    @Test
    public void isInvalidPhone_invalidPhone_returnFalse() {
        String phone = "";

        boolean isValidPhone = parameterValidationService.isValidPhone(phone);

        assertFalse(isValidPhone);
    }

    @Test
    public void isValidPaymentMethod_validPaymentMethod_returnTrue() {
        String paymentMethod = PaymentMethod.CASH.name();

        boolean isValidPaymentMethod = parameterValidationService.isValidPaymentMethod(paymentMethod);

        assertTrue(isValidPaymentMethod);
    }

    @Test
    public void isValidPaymentMethod_invalidPaymentMethod_returnFalse() {
        String paymentMethod = "";

        boolean isValidPaymentMethod = parameterValidationService.isValidPaymentMethod(paymentMethod);

        assertFalse(isValidPaymentMethod);
    }

    @Test
    public void isValidDeliveryDate_validDeliveryDate_returnTrue() {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(locale);
        String deliveryDate = LocalDate.now()
                .format(formatter);

        boolean isValidDeliveryDate = parameterValidationService.isValidDeliveryDate(deliveryDate, locale);

        assertTrue(isValidDeliveryDate);
    }

    @Test
    public void isValidDeliveryDate_invalidDeliveryDate_returnFalse() {
        String deliveryDate = "";

        boolean isValidDeliveryDate = parameterValidationService.isValidDeliveryDate(deliveryDate, locale);

        assertFalse(isValidDeliveryDate);
    }

    @Test
    public void isValidDeliveryDate_deliveryDateBeforeCurrentDate_returnTrue() {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(locale);
        String deliveryDate = LocalDate.now()
                .minusDays(1)
                .format(formatter);

        boolean isValidDeliveryDate = parameterValidationService.isValidDeliveryDate(deliveryDate, locale);

        assertFalse(isValidDeliveryDate);
    }

    @Test
    public void isValidRequiredData_validRequiredData_returnTrue() {
        String requiredData = "requiredData";

        boolean isValidRequiredData = parameterValidationService.isValidRequiredStringData(requiredData);

        assertTrue(isValidRequiredData);
    }

    @Test
    public void isValidRequiredData_invalidRequiredData_returnFalse() {
        String requiredData = "";

        boolean isValidRequiredData = parameterValidationService.isValidRequiredStringData(requiredData);

        assertFalse(isValidRequiredData);
    }

    @Test
    public void purseDate_returnCorrectDateValue() {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(locale);
        LocalDate deliveryDate = LocalDate.now();

        LocalDate date = parameterValidationService.parseDate(deliveryDate.format(formatter), locale);

        assertEquals(deliveryDate, date);
    }

    @Test
    public void parsePaymentMethod_returnPaymentMethodValue() {
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        PaymentMethod method = parameterValidationService.parsePaymentMethod(paymentMethod.name());

        assertEquals(paymentMethod, method);
    }

    @Test(expected = NumberFormatException.class)
    public void parseQuantity_zeroQuantity_NumberFormatException() throws ParseException {
        String quantity = "0";

        parameterValidationService.parseQuantity(quantity, locale);
    }

    @Test(expected = NumberFormatException.class)
    public void parseQuantity_notIntegerQuantity_NumberFormatException() throws ParseException {
        String quantity = "1,2";

        parameterValidationService.parseQuantity(quantity, locale);
    }

    @Test
    public void parseQuantity_validQuantity_returnQuantityValue() throws ParseException {
        int quantityValue;
        String quantity = "1.0";
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        quantityValue = parameterValidationService.parseQuantity(quantity, locale);

        assertEquals(quantityValue, numberFormat.parse(quantity)
                .intValue());
    }

}


