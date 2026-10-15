package com.orderplatform.orderservice.dto;

import java.math.BigDecimal;

public class ProductResponse {

    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final Integer stock;

    public ProductResponse(
            Long id,
            String name,
            BigDecimal price,
            Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }
}