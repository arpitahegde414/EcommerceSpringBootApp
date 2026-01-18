package com.example.inventoryservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false"
})
class InventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetInventory_Success() throws Exception {
        mockMvc.perform(get("/inventory/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1001))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.batches").isArray());
    }

    @Test
    void testGetInventory_NotFound() throws Exception {
        mockMvc.perform(get("/inventory/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateInventory_Success() throws Exception {
        String requestJson = "{\"batchId\":1,\"quantityToDeduct\":5}";

        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inventory updated successfully"));
    }
}
