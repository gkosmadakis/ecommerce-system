package com.example.analytics_service.service;

import com.example.analytics_service.domain.OrderEvent;
import com.example.analytics_service.domain.OrderMetrics;
import com.example.analytics_service.repository.OrderMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalyticsConsumer {

    private final OrderMetricsRepository repository;

    @KafkaListener(topics = "orders", groupId = "analytics-group")
    public void consume(OrderEvent event) {

        OrderMetrics metrics = new OrderMetrics();
        metrics.setOrderId(event.getOrderId());
        metrics.setStatus(event.getStatus());
        metrics.setProcessedAt(LocalDateTime.now());

        repository.save(metrics);
    }
}
