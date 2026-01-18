package com.example.orderservice.service;

import com.example.orderservice.client.InventoryServiceClient;
import com.example.orderservice.dto.*;
import com.example.orderservice.model.Orders;
import com.example.orderservice.model.Product;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryServiceClient inventoryClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Product product;
    private InventoryResponse inventoryResponse;
    private Orders order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        product = new Product();
        product.setProduct_id(1001L);
        product.setProductName("Laptop");

        orderRequest = new OrderRequest();
        orderRequest.setProductId(1001L);
        orderRequest.setQuantity(10);

        List<BatchDto> batches = Arrays.asList(
                new BatchDto(1L, 50, LocalDate.of(2026, 6, 25)),
                new BatchDto(2L, 30, LocalDate.of(2026, 8, 15))
        );
        inventoryResponse = new InventoryResponse(1001L, "Laptop", batches);

        order = new Orders();
        order.setId(1L);
        order.setProduct(product);
        order.setQuantity(10);
        order.setStatus(Orders.OrderStatus.PLACED);
        order.setOrderDate(LocalDate.now());
        order.setReservedBatchIds("1");
    }

    @Test
    @DisplayName("Place order successfully - quantity from single batch")
    void testPlaceOrder_Success_SingleBatch() {
        // Arrange
        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 10)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(inventoryResponse);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());
        when(orderRepository.save(any(Orders.class))).thenReturn(order);

        // Act
        OrderResponse response = orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(1001L, response.getProductId());
        assertEquals("Laptop", response.getProductName());
        assertEquals(10, response.getQuantity());
        assertEquals(Orders.OrderStatus.PLACED, response.getStatus());
        assertEquals("Order placed. Inventory reserved.", response.getMessage());
        assertNotNull(response.getReservedFromBatchIds());
        assertEquals(1, response.getReservedFromBatchIds().size());
        assertTrue(response.getReservedFromBatchIds().contains(1L));

        // Verify interactions
        verify(productRepository, times(1)).findById(1001L);
        verify(inventoryClient, times(1)).checkAvailability(1001L, 10);
        verify(inventoryClient, times(1)).getInventory(1001L);
        verify(inventoryClient, times(1)).updateInventory(1L, 10);
        verify(orderRepository, times(1)).save(any(Orders.class));
    }

    @Test
    @DisplayName("Place order successfully - quantity from multiple batches")
    void testPlaceOrder_Success_MultipleBatches() {
        // Arrange
        orderRequest.setQuantity(60);

        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 60)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(inventoryResponse);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());

        Orders savedOrder = new Orders();
        savedOrder.setId(2L);
        savedOrder.setProduct(product);
        savedOrder.setQuantity(60);
        savedOrder.setStatus(Orders.OrderStatus.PLACED);
        savedOrder.setOrderDate(LocalDate.now());
        savedOrder.setReservedBatchIds("1,2");

        when(orderRepository.save(any(Orders.class))).thenReturn(savedOrder);

        // Act
        OrderResponse response = orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());

        // Assert
        assertNotNull(response);
        assertEquals(2L, response.getOrderId());
        assertEquals(60, response.getQuantity());
        assertEquals(2, response.getReservedFromBatchIds().size());
        assertTrue(response.getReservedFromBatchIds().contains(1L));
        assertTrue(response.getReservedFromBatchIds().contains(2L));

        // Verify batch allocations
        verify(inventoryClient, times(1)).updateInventory(1L, 50); // First batch fully consumed
        verify(inventoryClient, times(1)).updateInventory(2L, 10); // Second batch partially consumed
    }

    @Test
    @DisplayName("Place order fails - product not found")
    void testPlaceOrder_ProductNotFound() {
        // Arrange
        when(productRepository.findById(1001L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());
        });

        assertEquals("Product not found: 1001", exception.getMessage());

        // Verify no inventory operations performed
        verify(productRepository, times(1)).findById(1001L);
        verify(inventoryClient, never()).checkAvailability(anyLong(), anyInt());
        verify(inventoryClient, never()).getInventory(anyLong());
        verify(inventoryClient, never()).updateInventory(anyLong(), anyInt());
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    @DisplayName("Place order fails - insufficient inventory")
    void testPlaceOrder_InsufficientInventory() {
        // Arrange
        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 10)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());
        });

        assertEquals("Insufficient inventory for product: 1001", exception.getMessage());

        // Verify no order created
        verify(productRepository, times(1)).findById(1001L);
        verify(inventoryClient, times(1)).checkAvailability(1001L, 10);
        verify(inventoryClient, never()).getInventory(anyLong());
        verify(inventoryClient, never()).updateInventory(anyLong(), anyInt());
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    @DisplayName("Place order fails - could not fulfill complete order")
    void testPlaceOrder_CannotFulfillCompleteOrder() {
        // Arrange
        orderRequest.setQuantity(100); // More than available (50+30=80)

        // Create inventory response with limited stock
        List<BatchDto> limitedBatches = Arrays.asList(
                new BatchDto(1L, 50, LocalDate.of(2026, 6, 25)),
                new BatchDto(2L, 30, LocalDate.of(2026, 8, 15))
        );
        InventoryResponse limitedInventory = new InventoryResponse(1001L, "Laptop", limitedBatches);

        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 100)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(limitedInventory);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());
        });

        assertEquals("Could not fulfill complete order quantity", exception.getMessage());

        // Verify partial inventory updates happened
        verify(inventoryClient, times(1)).updateInventory(1L, 50);
        verify(inventoryClient, times(1)).updateInventory(2L, 30);
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    @DisplayName("Place order - allocates from batch with earliest expiry (FIFO)")
    void testPlaceOrder_FIFO_EarliestExpiryFirst() {
        // Arrange
        orderRequest.setQuantity(5);

        // Batches already sorted by expiry date (earliest first)
        List<BatchDto> sortedBatches = Arrays.asList(
                new BatchDto(5L, 10, LocalDate.of(2026, 3, 31)),  // Earliest
                new BatchDto(1L, 50, LocalDate.of(2026, 6, 25))   // Later
        );
        InventoryResponse fifoInventory = new InventoryResponse(1001L, "Laptop", sortedBatches);

        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 5)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(fifoInventory);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());

        Orders savedOrder = new Orders();
        savedOrder.setId(3L);
        savedOrder.setProduct(product);
        savedOrder.setQuantity(5);
        savedOrder.setStatus(Orders.OrderStatus.PLACED);
        savedOrder.setOrderDate(LocalDate.now());
        savedOrder.setReservedBatchIds("5");

        when(orderRepository.save(any(Orders.class))).thenReturn(savedOrder);

        // Act
        OrderResponse response = orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());

        // Assert
        assertNotNull(response);
        assertTrue(response.getReservedFromBatchIds().contains(5L));

        // Verify earliest batch used first
        verify(inventoryClient, times(1)).updateInventory(5L, 5);
        verify(inventoryClient, never()).updateInventory(1L, anyInt());
    }

    @Test
    @DisplayName("Place order - skip batches with zero quantity")
    void testPlaceOrder_SkipZeroQuantityBatches() {
        // Arrange
        orderRequest.setQuantity(10);

        List<BatchDto> batchesWithZero = Arrays.asList(
                new BatchDto(1L, 0, LocalDate.of(2026, 3, 31)),   // Zero - should skip
                new BatchDto(2L, 50, LocalDate.of(2026, 6, 25))   // Available
        );
        InventoryResponse inventoryWithZero = new InventoryResponse(1001L, "Laptop", batchesWithZero);

        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 10)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(inventoryWithZero);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());

        Orders savedOrder = new Orders();
        savedOrder.setId(4L);
        savedOrder.setProduct(product);
        savedOrder.setQuantity(10);
        savedOrder.setStatus(Orders.OrderStatus.PLACED);
        savedOrder.setOrderDate(LocalDate.now());
        savedOrder.setReservedBatchIds("2");

        when(orderRepository.save(any(Orders.class))).thenReturn(savedOrder);

        // Act
        OrderResponse response = orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());

        // Assert
        assertEquals(1, response.getReservedFromBatchIds().size());
        assertTrue(response.getReservedFromBatchIds().contains(2L));

        // Verify zero batch was skipped
        verify(inventoryClient, never()).updateInventory(1L, anyInt());
        verify(inventoryClient, times(1)).updateInventory(2L, 10);
    }

    @Test
    @DisplayName("Place order - partial quantity from first batch, rest from second")
    void testPlaceOrder_PartialFromFirstBatch() {
        // Arrange
        orderRequest.setQuantity(60);

        List<BatchDto> batches = Arrays.asList(
                new BatchDto(1L, 45, LocalDate.of(2026, 3, 31)),  // Partial
                new BatchDto(2L, 50, LocalDate.of(2026, 6, 25))   // Remaining
        );
        InventoryResponse inventory = new InventoryResponse(1001L, "Laptop", batches);

        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 60)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(inventory);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());

        Orders savedOrder = new Orders();
        savedOrder.setId(5L);
        savedOrder.setProduct(product);
        savedOrder.setQuantity(60);
        savedOrder.setStatus(Orders.OrderStatus.PLACED);
        savedOrder.setOrderDate(LocalDate.now());
        savedOrder.setReservedBatchIds("1,2");

        when(orderRepository.save(any(Orders.class))).thenReturn(savedOrder);

        // Act
        OrderResponse response = orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());

        // Assert
        assertEquals(2, response.getReservedFromBatchIds().size());

        // Verify correct quantities deducted
        verify(inventoryClient, times(1)).updateInventory(1L, 45);  // Full first batch
        verify(inventoryClient, times(1)).updateInventory(2L, 15);  // Remaining 15 from second
    }

    @Test
    @DisplayName("Place order - inventory client throws exception")
    void testPlaceOrder_InventoryClientException() {
        // Arrange
        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 10))
                .thenThrow(new RuntimeException("Inventory service unavailable"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());
        });

        assertEquals("Inventory service unavailable", exception.getMessage());

        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    @DisplayName("Place order - order date is set to current date")
    void testPlaceOrder_OrderDateIsToday() {
        // Arrange
        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 10)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(inventoryResponse);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> {
            Orders savedOrder = invocation.getArgument(0);
            savedOrder.setId(10L);
            return savedOrder;
        });

        // Act
        LocalDate beforeOrder = LocalDate.now();
        OrderResponse response = orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());
        LocalDate afterOrder = LocalDate.now();

        // Assert - order date should be today
        verify(orderRepository, times(1)).save(argThat(order -> {
            LocalDate orderDate = order.getOrderDate();
            return !orderDate.isBefore(beforeOrder) && !orderDate.isAfter(afterOrder);
        }));
    }

    @Test
    @DisplayName("Place order - reserved batch IDs are comma-separated")
    void testPlaceOrder_ReservedBatchIdsFormat() {
        // Arrange
        orderRequest.setQuantity(60);

        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 60)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(inventoryResponse);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> {
            Orders savedOrder = invocation.getArgument(0);
            savedOrder.setId(11L);
            return savedOrder;
        });

        // Act
        orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());

        // Assert - verify batch IDs are saved as comma-separated string
        verify(orderRepository, times(1)).save(argThat(order -> {
            String batchIds = order.getReservedBatchIds();
            return batchIds != null && batchIds.contains(",") &&
                    batchIds.contains("1") && batchIds.contains("2");
        }));
    }

    @Test
    @DisplayName("Place order - order status is set to PLACED")
    void testPlaceOrder_StatusIsPlaced() {
        // Arrange
        when(productRepository.findById(1001L)).thenReturn(Optional.of(product));
        when(inventoryClient.checkAvailability(1001L, 10)).thenReturn(true);
        when(inventoryClient.getInventory(1001L)).thenReturn(inventoryResponse);
        doNothing().when(inventoryClient).updateInventory(anyLong(), anyInt());
        when(orderRepository.save(any(Orders.class))).thenReturn(order);

        // Act
        OrderResponse response = orderService.placeOrder(orderRequest.getProductId(), orderRequest.getQuantity());

        // Assert
        assertEquals(Orders.OrderStatus.PLACED, response.getStatus());

        verify(orderRepository, times(1)).save(argThat(order ->
                order.getStatus() == Orders.OrderStatus.PLACED
        ));
    }
}
