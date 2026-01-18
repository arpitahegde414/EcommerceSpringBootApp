package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.service.DefaultInventoryService;
import com.example.inventoryservice.service.InventoryHandler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    //build inventory service which is needed for the API
    private final InventoryHandler inventoryService;

    @GetMapping("/{productId}")
    @Operation(summary="Get Inventory by productId",
    description = "Returns list of inventory batches sorted by expiry date")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        try {
            InventoryResponse response = inventoryService.getInventoryByProductId(productId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update")
    @Operation(summary = "Update inventory",
            description = "Updates inventory after an order is placed")
    public ResponseEntity<Map<String, Object>> updateInventory(
            @RequestBody Map<String, Object> request) {
        try {
            Long batchId = Long.valueOf(request.get("batchId").toString());
            Integer quantityToDeduct = Integer.valueOf(request.get("quantityToDeduct").toString());

            inventoryService.updateInventory(batchId, quantityToDeduct);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Inventory updated successfully");
            response.put("batchId", batchId);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/check-availability")
    @Operation(summary = "Check availability",
            description = "Check if product quantity is available")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        boolean available = inventoryService.checkAvailability(productId, quantity);

        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("requestedQuantity", quantity);
        response.put("available", available);

        return ResponseEntity.ok(response);
    }
}
