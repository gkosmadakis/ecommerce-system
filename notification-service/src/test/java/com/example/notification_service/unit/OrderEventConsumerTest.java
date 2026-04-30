package com.example.notification_service.unit;

import com.example.notification_service.domain.OrderEvent;
import com.example.notification_service.service.NotificationService;
import com.example.notification_service.service.OrderEventConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class OrderEventConsumerTest {

    @Test
    void shouldConsumeEventAndTriggerNotifications() {

        NotificationService notificationService = mock(NotificationService.class);
        OrderEventConsumer consumer = new OrderEventConsumer(notificationService);

        OrderEvent event = new OrderEvent();
        event.setOrderId(100L);

        consumer.consume(event);

        verify(notificationService, times(1)).sendEmail(event);
        verify(notificationService, times(1)).sendSMS(event);
    }
}
