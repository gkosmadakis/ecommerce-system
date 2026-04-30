package com.example.analytics_service.unit;

import com.example.analytics_service.domain.OrderEvent;
import com.example.analytics_service.domain.OrderMetrics;
import com.example.analytics_service.repository.OrderMetricsRepository;
import com.example.analytics_service.service.AnalyticsConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsConsumerTest {

    @Mock
    private OrderMetricsRepository repository;

    @InjectMocks
    private AnalyticsConsumer consumer;

    @Test
    void shouldConvertEventToMetricsAndSave() {

        // given
        OrderEvent event = new OrderEvent();
        event.setOrderId(1L);
        event.setStatus("PROCESSED");

        ArgumentCaptor<OrderMetrics> captor = ArgumentCaptor.forClass(OrderMetrics.class);

        // when
        consumer.consume(event);

        // then
        verify(repository, times(1)).save(captor.capture());

        OrderMetrics saved = captor.getValue();

        assertEquals(1L, saved.getOrderId());
        assertEquals("PROCESSED", saved.getStatus());
        assertNotNull(saved.getProcessedAt());
        assertTrue(saved.getProcessedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
