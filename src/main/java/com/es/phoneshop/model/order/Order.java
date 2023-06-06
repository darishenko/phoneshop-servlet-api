package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.enam.order.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Order extends Cart {
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
    private BigDecimal subTotal;
    private BigDecimal deliveryCost;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate deliveryDate;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;
    private String formattedDeliveryDate;

    public Order() {
        super();
        this.subTotal = new BigDecimal(0);
        this.deliveryCost = new BigDecimal(0);
    }

    public Order(Locale locale) {
        super();
        this.subTotal = new BigDecimal(0);
        this.deliveryCost = new BigDecimal(0);
        this.dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", locale);
    }

    public Order(Locale locale, Cart cart, BigDecimal deliveryCost) {
        super();
        this.subTotal = cart.getTotalCost();
        this.deliveryCost = deliveryCost;
        this.dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", locale);
        this.setItems(cart.getItems());
        this.setTotalCost(subTotal.add(deliveryCost));
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public void setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public String getFormattedDeliveryDate() {
        return formattedDeliveryDate;
    }

    public void setFormattedDeliveryDate(String formattedDeliveryDate) {
        this.formattedDeliveryDate = formattedDeliveryDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
        this.formattedDeliveryDate = this.deliveryDate.format(dateFormatter);
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(BigDecimal deliveryCost) {
        this.deliveryCost = deliveryCost;
    }
}
