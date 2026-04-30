package com.example.order_service.repository;

import com.example.order_service.domain.OrderEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderEventRepository extends JpaRepository<OrderEventEntity, Long> {
    List<OrderEventEntity> findByOrderId(Long orderId);
}
