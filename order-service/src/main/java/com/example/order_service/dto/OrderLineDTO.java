package com.example.order_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderLineDTO {
    private String productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
