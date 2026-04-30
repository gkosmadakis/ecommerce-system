package com.example.order_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private String customerId;
    private List<OrderLineDTO> items;
}
