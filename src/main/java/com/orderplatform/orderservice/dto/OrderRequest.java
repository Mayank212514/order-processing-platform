
package com.orderplatform.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;

    public OrderRequest() {
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
