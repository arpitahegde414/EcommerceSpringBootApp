package com.example.orderservice.client;

import com.example.orderservice.dto.InventoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryServiceClientTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private InventoryServiceClient inventoryServiceClient;

    private static final String INVENTORY_SERVICE_URL = "http://localhost:8081";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the @Value field using ReflectionTestUtils
        ReflectionTestUtils.setField(inventoryServiceClient, "inventoryServiceUrl", INVENTORY_SERVICE_URL);
    }

    @Test
    @DisplayName("checkAvailability - RestClientException returns false (catch block)")
    void testCheckAvailability_RestClientException() {
        // Arrange
        String url = INVENTORY_SERVICE_URL + "/inventory/check-availability?productId=1001&quantity=10";
        when(restTemplate.getForObject(url, Map.class))
                .thenThrow(new RestClientException("Connection refused"));

        // Act
        boolean result = inventoryServiceClient.checkAvailability(1001L, 10);

        // Assert
        assertFalse(result);
        verify(restTemplate, times(1)).getForObject(url, Map.class);
    }

    @Test
    @DisplayName("getInventory - RestClientException throws RuntimeException (catch block)")
    void testGetInventory_RestClientException() {
        // Arrange
        String url = INVENTORY_SERVICE_URL + "/inventory/1001";
        when(restTemplate.getForObject(url, InventoryResponse.class))
                .thenThrow(new RestClientException("Service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryServiceClient.getInventory(1001L);
        });

        assertEquals("Failed to fetch inventory: Service unavailable", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(url, InventoryResponse.class);
    }

    @Test
    @DisplayName("updateInventory - RestClientException throws RuntimeException (catch block)")
    void testUpdateInventory_RestClientException() {
        // Arrange
        String url = INVENTORY_SERVICE_URL + "/inventory/update";
        Map<String, Object> request = new HashMap<>();
        request.put("batchId", 1L);
        request.put("quantityToDeduct", 10);

        when(restTemplate.postForObject(eq(url), any(Map.class), eq(Map.class)))
                .thenThrow(new RestClientException("Service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryServiceClient.updateInventory(1L, 10);
        });

        assertEquals("Failed to update inventory: Service unavailable", exception.getMessage());
        verify(restTemplate, times(1)).postForObject(eq(url), any(Map.class), eq(Map.class));
    }
}
