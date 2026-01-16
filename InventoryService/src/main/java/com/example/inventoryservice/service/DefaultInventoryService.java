package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.BatchDto;
import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.model.InventoryBatch;
import com.example.inventoryservice.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultInventoryService implements InventoryHandler {

    @Autowired
    private InventoryRepository inventoryRepository;

    public InventoryResponse getInventoryByProductId(Long productId) {
        log.info("Getting inventory batch by product id: "+ productId);
        List<InventoryBatch> batches = inventoryRepository.findByProduct_IdOrderByExpiryDateAsc(productId);

        if (batches.isEmpty()) {
            throw new RuntimeException("Product not found: " + productId);
        }
        String productName = batches.get(0).getProduct().getProductName();

        List<BatchDto> batchDtos = batches.stream().map(b->
                new BatchDto(b.getBatchId(), b.getQuantity(), b.getExpiryDate()))
                .collect(Collectors.toList());
        return new InventoryResponse(productId, productName, batchDtos);
    }

    @Override
    @Transactional
    public void updateInventory(Long batchId, Integer quantityToDeduct) {
        log.info("Updating inventory - Batch: {}, Deduct: {}", batchId, quantityToDeduct);

        InventoryBatch batch = inventoryRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found: " + batchId));

        if (batch.getQuantity() < quantityToDeduct) {
            throw new RuntimeException("Insufficient quantity in batch: " + batchId);
        }

        batch.setQuantity(batch.getQuantity() - quantityToDeduct);
        inventoryRepository.save(batch);

        log.info("Inventory updated - Batch: {}, Remaining: {}",
                batchId, batch.getQuantity());
    }

    @Override
    public boolean checkAvailability(Long productId, Integer quantity) {
        log.info("Checking availability - ProductId: {}, quanity: {}", productId, quantity);
        List<InventoryBatch> inventoryBatch = inventoryRepository.findByProduct_IdOrderByExpiryDateAsc(productId);
        int totalSum = inventoryBatch.stream()
                .filter(i-> i.getQuantity()>0)
                .mapToInt(InventoryBatch::getQuantity)
                .sum();
        if(totalSum>quantity)
            return true;
        return false;
    }
}
