package com.es.phoneshop.web.validation;

import com.es.phoneshop.enam.order.PaymentMethod;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

public interface ParameterValidationService {
    boolean isValidRequiredStringData(String data);

    boolean isValidDeliveryDate(String deliveryDate, Locale locale);

    boolean isValidPhone(String phone);

    boolean isValidPaymentMethod(String paymentMethod);

    LocalDate parseDate(String deliveryDate, Locale locale);

    PaymentMethod parsePaymentMethod(String paymentMethod);

    int parseQuantity(String quantity, Locale locale) throws ParseException;
}
