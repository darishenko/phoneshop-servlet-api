package com.es.phoneshop.web.validation.impl;

import com.es.phoneshop.enam.order.PaymentMethod;
import com.es.phoneshop.web.validation.ParameterValidationService;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class DefaultParameterValidationService implements ParameterValidationService {
    private static volatile ParameterValidationService instance;

    public static ParameterValidationService getInstance() {
        if (instance == null) {
            synchronized (DefaultParameterValidationService.class) {
                if (instance == null) {
                    instance = new DefaultParameterValidationService();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean isValidPhone(String phone) {
        Pattern phonePattern = Pattern.compile("\\d+");
        return !Objects.isNull(phone) && phonePattern.matcher(phone.trim()).matches();
    }

    @Override
    public boolean isValidPaymentMethod(String paymentMethod) {
        try {
            PaymentMethod.valueOf(paymentMethod);
        } catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isValidDeliveryDate(String deliveryDate, Locale locale) {
        try {
            LocalDate localDate = parseDate(deliveryDate, locale);
            if (localDate.isBefore(LocalDate.now())) {
                return false;
            }
        } catch (DateTimeParseException dateTimeParseException) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isValidRequiredStringData(String data) {
        return !Objects.isNull(data) && !data.trim().isEmpty();
    }

    @Override
    public LocalDate parseDate(String deliveryDate, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
        return LocalDate.parse(deliveryDate.trim(), formatter);
    }

    @Override
    public PaymentMethod parsePaymentMethod(String paymentMethod) {
        return PaymentMethod.valueOf(paymentMethod.trim());
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
