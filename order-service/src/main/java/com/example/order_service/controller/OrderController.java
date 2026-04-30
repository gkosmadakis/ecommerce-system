package com.example.order_service.controller;

import com.example.order_service.domain.Order;
import com.example.order_service.domain.OrderEventEntity;
import com.example.order_service.domain.OrderStatus;
import com.example.order_service.dto.OrderLineResponseDTO;
import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.dto.OrderResponseDTO;
import com.example.order_service.dto.UpdateStatusRequest;
import com.example.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody @Valid OrderRequestDTO dto) {
        Order order = orderService.createOrder(dto);
        // Map Entity to DTO
        List<OrderLineResponseDTO> lines = getOrderLineResponseDTOS(order);
        OrderResponseDTO response = new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().toString(),
                order.getOrderDate(),
                order.getTotalAmount(),
                lines
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getById(@PathVariable("id") Long id) {
        Order order = orderService.getOrder(id);
        // 1. Map the OrderLines to DTOs
        List<OrderLineResponseDTO> lines = getOrderLineResponseDTOS(order);

        // 2. Wrap everything in the OrderResponseDTO
        OrderResponseDTO response = new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().toString(),
                order.getOrderDate(),
                order.getTotalAmount(),
                lines
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable("id") Long id,
                                                        @RequestBody @Valid OrderRequestDTO dto) {

        Order order = orderService.updateOrder(id, dto);

        // Map updated Entity to DTO
        List<OrderLineResponseDTO> lines = order.getOrderLines().stream()
                .map(line -> new OrderLineResponseDTO(
                        line.getId(),
                        line.getProductId(),
                        line.getQuantity(),
                        line.getUnitPrice(),
                        line.getLineTotal()
                )).toList();

        OrderResponseDTO response = new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().toString(),
                order.getOrderDate(),
                order.getTotalAmount(),
                lines
        );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> list(Pageable pageable) {
        Page<Order> ordersPage = orderService.listOrders(pageable);

        // Use Page.map to convert each Entity to a DTO
        Page<OrderResponseDTO> responsePage = ordersPage.map(order -> {

            // Map the inner lines first
            List<OrderLineResponseDTO> lines = order.getOrderLines().stream()
                    .map(line -> new OrderLineResponseDTO(
                            line.getId(),
                            line.getProductId(),
                            line.getQuantity(),
                            line.getUnitPrice(),
                            line.getLineTotal()
                    )).toList();

            // Return the mapped DTO for this specific order
            return new OrderResponseDTO(
                    order.getId(),
                    order.getCustomerId(),
                    order.getStatus().toString(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    lines
            );
        });

        return ResponseEntity.ok(responsePage);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable("id") Long id,
            @RequestBody UpdateStatusRequest request) {

        orderService.updateStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<OrderEventEntity>> getHistory(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.getOrderHistory(id));
    }

    private static List<OrderLineResponseDTO> getOrderLineResponseDTOS(Order order) {
        return order.getOrderLines().stream()
                .map(line -> new OrderLineResponseDTO(
                        line.getId(),
                        line.getProductId(),
                        line.getQuantity(),
                        line.getUnitPrice(),
                        line.getLineTotal()
                )).toList();
    }
}
