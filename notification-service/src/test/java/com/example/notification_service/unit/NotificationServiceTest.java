package com.example.notification_service.unit;

import com.example.notification_service.domain.OrderEvent;
import com.example.notification_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldCallEmailFallbackWhenExceptionOccurs() {

        OrderEvent event = new OrderEvent();
        event.setOrderId(1L);

        Throwable ex = new RuntimeException("Email failure");

        // direct fallback invocation test
        assertDoesNotThrow(() ->
                notificationService.emailFallback(event, ex)
        );
    }

    @Test
    void shouldCallSmsFallbackWhenExceptionOccurs() {

        OrderEvent event = new OrderEvent();
        event.setOrderId(2L);

        Throwable ex = new RuntimeException("SMS failure");

        assertDoesNotThrow(() ->
                notificationService.smsFallback(event, ex)
        );
    }
}