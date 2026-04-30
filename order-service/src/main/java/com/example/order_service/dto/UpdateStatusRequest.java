package com.example.order_service.dto;

import com.example.order_service.domain.OrderStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private OrderStatus status;
}