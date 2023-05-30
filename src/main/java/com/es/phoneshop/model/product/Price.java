package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Price implements Serializable {
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
    private LocalDate startDate;
    private BigDecimal price;
    private String formattedStartDate;

    public Price(BigDecimal price) {
        this.price = price;
        this.startDate = LocalDate.now();
        this.formattedStartDate = this.startDate.format(dateFormatter);
    }

    public Price(BigDecimal price, LocalDate startDate) {
        this.price = price;
        this.startDate = startDate;
        this.formattedStartDate = this.startDate.format(dateFormatter);
    }

    public DateTimeFormatter getDateFormatter() {
        return Price.dateFormatter;
    }

    public void setDateFormatter(DateTimeFormatter dateFormatter) {
        Price.dateFormatter = dateFormatter;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getFormattedStartDate() {
        return formattedStartDate;
    }

    public void setFormattedStartDate(String formattedStartDate) {
        this.formattedStartDate = formattedStartDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
