package com.es.phoneshop.web.validation.impl;

import com.es.phoneshop.enam.order.PaymentMethod;
import com.es.phoneshop.web.constant.ServletConstant.*;
import com.es.phoneshop.web.validation.ParameterValidationService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Map;
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
            parsePaymentMethod(paymentMethod);
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

        validateIntegerNumber(quantityNumber);
        validatePositiveNumber(quantityNumber);

        return quantityNumber.intValue();
    }

    @Override
    public BigDecimal parsePrice(String price, String priceParameterName, Map<String, String> priceErrors, Locale locale) {
        if (price.isEmpty()){
            return null;
        }
        BigDecimal priceValue = null;
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(locale);
            Number priceNumber = numberFormat.parse(price);
            validatePositiveNumber(priceNumber);
            priceValue = BigDecimal.valueOf(priceNumber.doubleValue());
        } catch (NumberFormatException numberFormatException) {
            priceErrors.put(priceParameterName, numberFormatException.getMessage());
        } catch (ParseException parseException) {
            priceErrors.put(priceParameterName, Message.Error.NOT_A_NUMBER);
        }
        return priceValue;
    }

    @Override
    public void validateMinMaxPrices(BigDecimal minPrice, BigDecimal maxPrice, Map<String, String> priceErrors) {
        if (Objects.nonNull(minPrice) && Objects.nonNull(maxPrice) && minPrice.doubleValue() > maxPrice.doubleValue()) {
            priceErrors.put(RequestAttribute.PRICE_ERROR, Message.Error.MIN_LESS_MAX_PRICE);
        }
    }

    private void validatePositiveNumber(Number number){
        if (number.doubleValue() < 0) {
            throw new NumberFormatException("Must be a positive number");
        }
    }

    private void validateIntegerNumber(Number number){
        if (number.doubleValue() != number.intValue()) {
            throw new NumberFormatException("Must be an integer number");
        }
    }

}
