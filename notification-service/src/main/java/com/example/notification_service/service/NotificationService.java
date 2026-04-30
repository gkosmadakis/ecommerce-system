package com.example.notification_service.service;

import com.example.notification_service.domain.OrderEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Retry(name = "notificationRetry", fallbackMethod = "emailFallback")
    public void sendEmail(OrderEvent event) {
        log.info("Sending EMAIL for order {}", event.getOrderId());

        // simulate failure
        if (Math.random() < 0.7) {
            throw new RuntimeException("Email failed");
        }
    }

    @Retry(name = "notificationRetry", fallbackMethod = "smsFallback")
    public void sendSMS(OrderEvent event) {
        log.info("Sending SMS for order {}", event.getOrderId());

        if (Math.random() < 0.5) {
            throw new RuntimeException("SMS failed");
        }
    }

    public void emailFallback(OrderEvent event, Throwable t) {
        log.error("EMAIL failed after retries for order {}", event.getOrderId(), t);
    }

    public void smsFallback(OrderEvent event, Throwable t) {
        log.error("SMS failed after retries for order {}", event.getOrderId(), t);
    }
}
