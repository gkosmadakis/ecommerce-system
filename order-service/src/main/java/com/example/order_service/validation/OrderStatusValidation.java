package com.example.order_service.validation;

import com.example.order_service.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusValidation {

    public void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot change status after SHIPPED");
        }

        if (current == OrderStatus.UNPROCESSED && next != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Invalid transition");
        }
    }
}
