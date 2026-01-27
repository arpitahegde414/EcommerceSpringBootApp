package com.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderAssignmentRequest {
    private Long userId;
    private Long orderId;
    private Integer totalAmount;
}
