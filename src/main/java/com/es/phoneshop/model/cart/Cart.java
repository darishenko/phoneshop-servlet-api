package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.Item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cart extends Item implements Serializable, Cloneable {
    private List<CartItem> items;
    private int totalQuantity;
    private BigDecimal totalCost;

    public Cart() {
        super();
        this.items = new ArrayList<>();
        totalCost = new BigDecimal(0);
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return items.equals(cart.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
    }

    @Override
    public String toString() {
        return "Cart {" + items.toString() + "}";
    }

    @Override
    public Cart clone() {
        try {
            Cart cart = (Cart) super.clone();
            cart.items = this.items.stream()
                    .map(CartItem::clone)
                    .collect(Collectors.toList());
            return cart;
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new RuntimeException("Clone is not supported");
        }
    }

}
