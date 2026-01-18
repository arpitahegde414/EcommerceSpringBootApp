package com.example.orderservice.client;

import com.example.orderservice.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceClient {
//    private final RestTemplate restTemplate;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public boolean checkAvailability(Long productId, Integer quantity){
        String checkAvailabilityUrl = inventoryServiceUrl+"/inventory/check-availability";
        log.info("Checking the Availibility for productId: "+productId+" Quantity: "+quantity+" " +
                " By hiting url: "+ checkAvailabilityUrl);

        try{
//            Map<String, Object> response = restTemplate.getForObject(checkAvailabilityUrl, Map.class);
//            return response!=null && (Boolean) response.get("available");
            return false;
        }catch(Exception e){
            log.error("Error checking availability", e);
            return false;
        }
    }

    public InventoryResponse getInventory(Long productId) {
        log.info("Getting inventory for product id: "+ productId);
        String getInventoryUrl = inventoryServiceUrl+"/"+productId;
        try{
//           return restTemplate.getForObject(getInventoryUrl,
//                    InventoryResponse.class);
            return null;
        }
        catch ( Exception e){
            log.error("Error calling Inventory Service", e);
            throw new RuntimeException("Failed to fetch inventory: " + e.getMessage());
        }
    }

    public void updateInventory(Long batchId, Integer quantityFromBatch) {
        log.info("Updating inventory for batchId: "+ batchId+" Quantity: "+ quantityFromBatch);
        String updateInventoryUrl = inventoryServiceUrl+"/update";
        Map<String,Object> request = new HashMap<>();
        request.put("batchId", batchId);
        request.put("quantityToDeduct", quantityFromBatch);
        try{
//            restTemplate.postForObject(updateInventoryUrl, request, Map.class);
            log.info("Updated inventory");
        }
        catch (Exception e){
            log.error("Error updating Inventory Service", e);
            throw new RuntimeException("Failed to update inventory: " + e.getMessage());
        }
    }
}
