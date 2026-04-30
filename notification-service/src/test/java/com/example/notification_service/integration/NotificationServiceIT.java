package com.example.notification_service.integration;

import com.example.notification_service.domain.OrderEvent;
import com.example.notification_service.service.NotificationService;
import com.example.notification_service.service.OrderEventConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
class NotificationServiceIT {

    @Autowired
    private OrderEventConsumer orderEventConsumer;

    @MockitoBean
    private NotificationService notificationService; // Mock the actual "sending" part if needed

    @Test
    void shouldProcessOrderEventSuccessfully() {
        // GIVEN: A valid event
        OrderEvent event = new OrderEvent();
        event.setOrderId(999L);
        event.setStatus("CREATED");

        // WHEN: We manually trigger the consumer method
        // (Just like Kafka would do in production)
        orderEventConsumer.consume(event);

        // THEN: Verify the downstream logic was triggered
        // This proves the logic handles the event correctly
        verify(notificationService, times(1)).sendEmail(any());
        verify(notificationService, times(1)).sendSMS(any());
    }
}
