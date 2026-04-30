package com.example.analytics_service.repository;

import com.example.analytics_service.domain.OrderMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderMetricsRepository extends MongoRepository<OrderMetrics, String> {
    OrderMetrics findByOrderId(Long orderId);
}
