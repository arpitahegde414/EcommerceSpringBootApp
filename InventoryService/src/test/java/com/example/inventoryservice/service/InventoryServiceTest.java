package com.example.inventoryservice.service;

import com.example.inventoryservice.model.Product;
import org.junit.jupiter.api.Test;

import com.example.inventoryservice.dto.*;
import com.example.inventoryservice.model.InventoryBatch;
import com.example.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryRepository repository;

    @InjectMocks
    private DefaultInventoryService inventoryService;

    private List<InventoryBatch> sampleBatches;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Product p1 = new Product(1001L, "Laptop");
        sampleBatches = Arrays.asList(
                new InventoryBatch(1L, p1, 50, LocalDate.of(2026, 6, 25)),
                new InventoryBatch(2L, p1, 30, LocalDate.of(2026, 8, 15))
        );
    }

    @Test
    void testGetInventoryByProductId_Success() {
        when(repository.findByProduct_IdOrderByExpiryDateAsc(1001L))
                .thenReturn(sampleBatches);

        InventoryResponse response = inventoryService.getInventoryByProductId(1001L);

        assertNotNull(response);
        assertEquals(1001L, response.getProductId());
        assertEquals(2, response.getBatches().size());

        verify(repository, times(1)).findByProduct_IdOrderByExpiryDateAsc(1001L);
    }

    @Test
    void testGetInventoryByProductId_ProductNotFound() {
        when(repository.findByProduct_IdOrderByExpiryDateAsc(9999L))
                .thenReturn(Arrays.asList());

        assertThrows(RuntimeException.class, () -> {
            inventoryService.getInventoryByProductId(9999L);
        });
    }

    @Test
    void testUpdateInventory_Success() {
        InventoryBatch batch = sampleBatches.get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(batch));
        when(repository.save(any(InventoryBatch.class))).thenReturn(batch);

        inventoryService.updateInventory(1L, 10);

        assertEquals(40, batch.getQuantity());
        verify(repository, times(1)).save(batch);
    }

    @Test
    void testUpdateInventory_InsufficientQuantity() {
        InventoryBatch batch = sampleBatches.get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(batch));

        assertThrows(RuntimeException.class, () -> {
            inventoryService.updateInventory(1L, 100);
        });
    }

    @Test
    void testCheckAvailability_Available() {
        when(repository.findByProduct_IdOrderByExpiryDateAsc(1001L))
                .thenReturn(sampleBatches);

        boolean available = inventoryService.checkAvailability(1001L, 60);

        assertTrue(available);
    }

    @Test
    void testCheckAvailability_NotAvailable() {
        when(repository.findByProduct_IdOrderByExpiryDateAsc(1001L))
                .thenReturn(sampleBatches);

        boolean available = inventoryService.checkAvailability(1001L, 100);

        assertFalse(available);
    }
}
