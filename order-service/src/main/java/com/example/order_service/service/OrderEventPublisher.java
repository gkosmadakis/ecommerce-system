package com.example.order_service.service;

import com.example.order_service.domain.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private static final String TOPIC = "orders";

    public void publishOrderCreated(Long orderId) {
        OrderEvent event = new OrderEvent(
                "ORDER_CREATED",
                orderId,
                "UNPROCESSED",
                LocalDateTime.now()
        );

        kafkaTemplate.send(TOPIC, event);
    }

    public void publishOrderUpdated(Long orderId, String status) {
        kafkaTemplate.send("orders",
                new OrderEvent("ORDER_UPDATED", orderId, status, LocalDateTime.now()));
    }

    public void publishOrderProcessed(Long orderId) {
        kafkaTemplate.send("orders",
                new OrderEvent("ORDER_PROCESSED", orderId, "PROCESSED", LocalDateTime.now()));
    }
}
