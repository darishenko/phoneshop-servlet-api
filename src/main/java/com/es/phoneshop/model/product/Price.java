package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Price {
    private LocalDate startDate;
    private BigDecimal price;
    private String formattedStartDate;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

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
        return dateFormatter;
    }

    public void setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
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
