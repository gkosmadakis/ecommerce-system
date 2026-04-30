package com.example.order_service.support.builder;

import com.example.order_service.dto.OrderLineDTO;
import com.example.order_service.dto.OrderRequestDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderRequestBuilder {

    private String customerId = "1";
    private final List<OrderLineDTO> items = new ArrayList<>();

    public static OrderRequestBuilder builder() {
        return new OrderRequestBuilder();
    }

    public OrderRequestBuilder withCustomer(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderRequestBuilder addItem(String productId, int qty, BigDecimal price) {
        OrderLineDTO dto = new OrderLineDTO();
        dto.setProductId(productId);
        dto.setQuantity(qty);
        dto.setUnitPrice(price);
        items.add(dto);
        return this;
    }

    public OrderRequestDTO build() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setCustomerId(customerId);
        dto.setItems(items);
        return dto;
    }
}
