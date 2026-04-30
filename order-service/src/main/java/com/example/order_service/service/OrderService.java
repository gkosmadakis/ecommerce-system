package com.example.order_service.service;

import com.example.order_service.domain.*;
import com.example.order_service.dto.OrderLineDTO;
import com.example.order_service.dto.OrderRequestDTO;
import com.example.order_service.repository.InventoryRepository;
import com.example.order_service.repository.OrderEventRepository;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.validation.InventoryValidation;
import com.example.order_service.validation.OrderStatusValidation;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventRepository eventRepository;
    private final OrderEventPublisher publisher;
    private final OrderStatusValidation orderStatusValidation;
    private final InventoryValidation inventoryValidation;
    private final InventoryRepository inventoryRepository;

    @Transactional("transactionManager")
    public Order createOrder(OrderRequestDTO dto) {

        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setStatus(OrderStatus.UNPROCESSED);
        order.setOrderDate(LocalDateTime.now());
        List<OrderLine> lines = new ArrayList<>();
        for (OrderLineDTO item : dto.getItems()) {

            // 1. Validate stock
            inventoryValidation.validateInventory(
                    item.getProductId(),
                    item.getQuantity()
            );
            // 2. Decrease stock
            decreaseStock(item.getProductId(), item.getQuantity());
            lines.add(createOrderLine(order, item));
        }
        order.setOrderLines(lines);
        // 3. Calculate total
        BigDecimal total = lines.stream()
                .map(OrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        publisher.publishOrderCreated(saved.getId());

        return saved;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public Order updateOrder(Long id, OrderRequestDTO dto) {
        Order order = getOrder(id);
        // Business rule: cannot update shipped orders
        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot update a shipped order");
        }
        // Update customer (optional)
        order.setCustomerId(dto.getCustomerId());
        //  Clear existing lines
        order.getOrderLines().clear();
        //  Rebuild order lines
        List<OrderLine> newLines = dto.getItems().stream().map(item -> {
            //  Inventory validation (important requirement)
            inventoryValidation.validateInventory(item.getProductId(), item.getQuantity());
            return createOrderLine(order, item);
        }).toList();

        order.setOrderLines(newLines);
        //  Recalculate total
        BigDecimal total = newLines.stream()
                .map(OrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        // Publish event
        publisher.publishOrderUpdated(saved.getId(), saved.getStatus().name());

        return saved;
    }

    @Transactional
    public void updateStatus(Long id, OrderStatus status) {
        Order order = getOrder(id);
        orderStatusValidation.validateStatusTransition(order.getStatus(), status);
        order.setStatus(status);
        orderRepository.save(order);
        recordEvent(order); // event sourcing
        publisher.publishOrderUpdated(order.getId(), status.name());
    }

    public List<OrderEventEntity> getOrderHistory(Long orderId) {
        getOrder(orderId);

        return eventRepository.findByOrderId(orderId);
    }

    public Page<Order> listOrders(Pageable pageable) {
        return orderRepository.findByDeletedFalse(pageable);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrder(id);
        order.setDeleted(true);
        orderRepository.save(order);
    }

    private void recordEvent(Order order) {
        OrderEventEntity event = new OrderEventEntity();
        event.setOrderId(order.getId());
        event.setStatus(order.getStatus().name());
        event.setTimestamp(LocalDateTime.now());

        eventRepository.save(event);
    }

    private void decreaseStock(String productId, int qty) {
        Inventory inventory = inventoryRepository.findById(productId).orElseThrow();
        inventory.setAvailableStock(inventory.getAvailableStock() - qty);
        inventoryRepository.save(inventory);
    }

    @NotNull
    private OrderLine createOrderLine(Order order, OrderLineDTO item) {
        OrderLine line = new OrderLine();
        line.setProductId(item.getProductId());
        line.setQuantity(item.getQuantity());
        line.setUnitPrice(item.getUnitPrice());
        line.setLineTotal(
                item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        );
        line.setOrder(order);

        return line;
    }
}
