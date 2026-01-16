package com.example.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.inventoryservice.dto.BatchDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private Long productId;
    private String name;
    private List<BatchDto> batches;
}
