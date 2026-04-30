package com.example.analytics_service.integration;

import com.example.analytics_service.domain.OrderEvent;
import com.example.analytics_service.domain.OrderMetrics;
import com.example.analytics_service.repository.OrderMetricsRepository;
import com.example.analytics_service.service.AnalyticsConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@Testcontainers
class AnalyticsConsumerIT {
    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:latest");

    // 2. Map the dynamic port to Spring's configuration
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }
    @Autowired
    private AnalyticsConsumer orderEventConsumer;

    @Autowired
    private OrderMetricsRepository repository;

    @Test
    void shouldProcessOrderEventAndPersistMetrics() {
        // GIVEN
        OrderEvent event = new OrderEvent();
        event.setOrderId(100L);
        event.setStatus("SHIPPED");

        // WHEN: Manually trigger the consumer logic
        orderEventConsumer.consume(event);

        // THEN: Verify the data was actually saved to the real test database
        OrderMetrics metrics = repository.findByOrderId(100L);

        assertNotNull(metrics, "Metrics should have been persisted to the database");
        assertEquals("SHIPPED", metrics.getStatus());
        assertEquals(100L, metrics.getOrderId());
        assertNotNull(metrics.getProcessedAt());
    }
}