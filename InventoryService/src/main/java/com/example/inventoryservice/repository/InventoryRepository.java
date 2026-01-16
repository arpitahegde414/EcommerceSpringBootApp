package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryBatch, Long> {

    List<InventoryBatch> findByProduct_IdOrderByExpiryDateAsc(Long product_id);

}
