package com.example.analytics_service.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "order_metrics")
@Data
public class OrderMetrics {

    @Id
    private String id;

    private Long orderId;
    private String status;
    private LocalDateTime processedAt;
}
