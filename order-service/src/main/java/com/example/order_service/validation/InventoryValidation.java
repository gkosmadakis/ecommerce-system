package com.example.order_service.validation;

import com.example.order_service.domain.Inventory;
import com.example.order_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryValidation {
    private final InventoryRepository inventoryRepository;

    public void validateInventory(String productId, int requestedQty) {

        Inventory inventory = inventoryRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + productId)
                );

        if (inventory.getAvailableStock() < requestedQty) {
            throw new IllegalStateException(
                    "Insufficient stock for product " + productId
            );
        }
    }
}
