package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;

public interface InventoryHandler {

    InventoryResponse getInventoryByProductId(Long productId);

    void updateInventory(Long batchId, Integer quantityToDeduct);

    boolean checkAvailability(Long productId, Integer quantity);
}
