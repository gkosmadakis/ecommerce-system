package com.example.order_service.unit;

import com.example.order_service.domain.*;
import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.repository.*;
import com.example.order_service.service.*;
import com.example.order_service.validation.InventoryValidation;
import com.example.order_service.validation.OrderStatusValidation;
import com.example.order_service.support.builder.OrderRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderEventRepository eventRepository;
    @Mock private OrderEventPublisher publisher;
    @Mock private OrderStatusValidation statusValidation;
    @Mock private InventoryValidation inventoryValidation;
    @Mock private InventoryRepository inventoryRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        // given
        OrderRequestDTO request = OrderRequestBuilder.builder()
                .withCustomer("1")
                .addItem("P1", 2, BigDecimal.valueOf(10))
                .build();
        Inventory inventory = new Inventory();
        inventory.setProductId("P1");
        inventory.setAvailableStock(10);

        when(inventoryRepository.findById("P1"))
                .thenReturn(Optional.of(inventory));

        when(orderRepository.save(any())).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L); // 👈 simulate DB-generated ID
            return order;
        });

        // when
        Order result = orderService.createOrder(request);

        // then
        assertNotNull(result);
        assertEquals(OrderStatus.UNPROCESSED, result.getStatus());
        assertEquals(BigDecimal.valueOf(20), result.getTotalAmount());
        verify(publisher).publishOrderCreated(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> orderService.getOrder(1L));
    }

    @Test
    void shouldNotAllowUpdateOnShippedOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        OrderRequestDTO dto = OrderRequestBuilder.builder().build();
        assertThrows(IllegalStateException.class,
                () -> orderService.updateOrder(1L, dto));
    }

    @Test
    void shouldDeleteOrderSoftly() {
        Order order = new Order();
        order.setDeleted(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        orderService.deleteOrder(1L);
        assertTrue(order.isDeleted());
        verify(orderRepository).save(order);
    }
}
