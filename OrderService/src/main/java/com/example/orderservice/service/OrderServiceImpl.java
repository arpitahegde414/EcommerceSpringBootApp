package com.example.orderservice.service;

import com.example.orderservice.client.InventoryServiceClient;
import com.example.orderservice.dto.BatchDto;
import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.model.Product;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.model.Orders;
import com.example.orderservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryServiceClient inventoryServiceClient;

    @Transactional
    @Override
    public OrderResponse placeOrder(Long productId, Integer quantity) {
        log.info("Starting to place order for productId: "+productId+" Quantity: "+quantity);
        //1. check if product id exists
        Optional<Product> p = productRepository.findById(productId);
        if(p.isEmpty()){
            throw new RuntimeException("Product not found: " + productId);
        }
        //1.check if product id and quantity exists
        if(!inventoryServiceClient.checkAvailability(productId, quantity)){
            throw new RuntimeException("Insufficient inventory for product: " + productId);
        }
        //2. Get all batches for the product id
        InventoryResponse inventory = inventoryServiceClient.getInventory(productId);
        //3. get the batches in the order of expiry date and store it in order object
        List<Long> reservedBatchIds = new ArrayList<>();
        Integer remainingQuantity = quantity;
        for(BatchDto batch: inventory.getBatches()){
            if(remainingQuantity<=0)
                break;
            Integer quantityFromBatch = Math.min(batch.getQuantity(), remainingQuantity);

            if(quantityFromBatch>0){
                inventoryServiceClient.updateInventory(batch.getBatchId(), quantityFromBatch);
                reservedBatchIds.add(batch.getBatchId());
                remainingQuantity -= quantityFromBatch;

                log.info("Reserved {} units from batch {}", quantityFromBatch, batch.getBatchId());
            }
        }
        //couldnt fulfill the order, rare case
        if (remainingQuantity > 0) {
            throw new RuntimeException("Could not fulfill complete order quantity");
        }
        // 4. Create order
        Orders order = new Orders();
        Product p1 = new Product();
        p1.setProduct_id(productId);
        p1.setProductName(inventory.getProductName());
        order.setProduct(p1);
        order.setQuantity(quantity);
        order.setStatus(Orders.OrderStatus.PLACED);
        order.setOrderDate(LocalDate.now());
        order.setReservedBatchIds(
                reservedBatchIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","))
        );

        Orders savedOrder = orderRepository.save(order);
        log.info("Order created successfully - OrderId: {}", savedOrder.getId());

        // 5. Build response
        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getProduct().getProduct_id(),
                savedOrder.getProduct().getProductName(),
                savedOrder.getQuantity(),
                savedOrder.getStatus(),
                reservedBatchIds,
                "Order placed. Inventory reserved."
        );

    }
}
