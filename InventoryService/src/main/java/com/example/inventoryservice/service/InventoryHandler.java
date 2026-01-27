package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.model.Product;

import java.util.List;

public interface InventoryHandler {

    InventoryResponse getInventoryByProductId(Long productId);

    void updateInventory(Long batchId, Integer quantityToDeduct);

    boolean checkAvailability(Long productId, Integer quantity);

    List<Product> getAllProducts();
}
