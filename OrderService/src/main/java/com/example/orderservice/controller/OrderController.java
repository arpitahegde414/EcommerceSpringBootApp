package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request){
        try{
            //validate request
            if(request.getProductId() == null || request.getQuantity() == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Product ID and quantity are required");
                return ResponseEntity.badRequest().body(error);
            }

            if (request.getQuantity() <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Quantity must be greater than 0");
                return ResponseEntity.badRequest().body(error);
            }
            OrderResponse orderResponse = orderService.placeOrder(request.getProductId(), request.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
        }
        catch (Exception e){
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
