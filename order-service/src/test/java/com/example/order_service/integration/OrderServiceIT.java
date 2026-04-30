package com.example.order_service.integration;

import com.example.order_service.domain.Inventory;
import com.example.order_service.domain.Order;
import com.example.order_service.domain.OrderEvent;
import com.example.order_service.repository.InventoryRepository;
import com.example.order_service.service.OrderEventPublisher;
import com.example.order_service.service.OrderService;
import com.example.order_service.support.builder.OrderRequestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
)
@Testcontainers
@ActiveProfiles("test")
class OrderServiceIT {
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");


    @MockitoBean
    private OrderEventPublisher orderEventPublisher;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Force the app to use the Testcontainer ports
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired private OrderService orderService;
    @Autowired private InventoryRepository inventoryRepository;

    @Test
    void shouldPersistOrderAndReduceInventory() {

        // given inventory in DB
        Inventory inv = new Inventory();
        inv.setProductId("P1");
        inv.setAvailableStock(10);
        inventoryRepository.save(inv);

        var request = OrderRequestBuilder.builder()
                .addItem("P1", 2, BigDecimal.valueOf(15))
                .build();

        // when
        Order order = orderService.createOrder(request);

        // then
        assertNotNull(order.getId());

        Inventory updated = inventoryRepository.findById("P1").orElseThrow();
        assertEquals(8, updated.getAvailableStock());
        verify(orderEventPublisher).publishOrderCreated(any());
    }
}