package com.example.orderservice.dto;

import com.example.orderservice.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Order.OrderStatus status;
    private List<Long> reservedFromBatchIds;
    private String message;
}