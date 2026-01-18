package com.example.orderservice.controller;

import com.example.orderservice.client.InventoryServiceClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false"
})
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private InventoryServiceClient inventoryClient;

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
}