package com.example.orderservice.controller;

import com.example.orderservice.client.InventoryServiceClient;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void testPlaceOrder_InvalidRequest() throws Exception {
        String requestJson = "{\"productId\":null,\"quantity\":10}";

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testPlaceOrder_NegativeQuantity() throws Exception {
        String requestJson = "{\"productId\":1001,\"quantity\":-5}";

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testPlaceOrder_Success() throws Exception {
        OrderRequest request =  new OrderRequest(1001L, 10);
        when(orderService.placeOrder(anyLong(), anyInt())).thenReturn(null);
        orderController.placeOrder(request);
    }

    @Test
    void testPlaceOrder_CatchBlock_ServiceException() throws Exception {
        // Arrange
        String requestJson = "{\"productId\":1001,\"quantity\":10}";

//        // Mock the service to throw an exception


        // Act & Assert
        mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Insufficient inventory for product: 1001"));
    }
}