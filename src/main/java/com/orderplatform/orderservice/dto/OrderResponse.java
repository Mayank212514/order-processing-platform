
package com.orderplatform.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

    private final Long id;
    private final BigDecimal totalAmount;
    private final String status;
    private final LocalDateTime createdAt;

    public OrderResponse(
            Long id,
            BigDecimal totalAmount,
            String status,
            LocalDateTime createdAt) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
