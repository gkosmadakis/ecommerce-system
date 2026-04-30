package com.example.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineResponseDTO {
    Long id;
    String productId;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal lineTotal;
}
