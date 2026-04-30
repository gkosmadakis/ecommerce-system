package com.example.notification_service.service;


import com.example.notification_service.domain.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void consume(OrderEvent event) {
        log.info("Received event: {}", event);

        notificationService.sendEmail(event);
        notificationService.sendSMS(event);
    }

}
