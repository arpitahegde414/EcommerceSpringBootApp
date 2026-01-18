package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryBatch, Long> {

    @Query("SELECT ib FROM InventoryBatch ib WHERE ib.product.product_id = :product_id ORDER BY ib.expiryDate ASC")
    List<InventoryBatch> findByProduct_IdOrderByExpiryDateAsc(Long product_id);

}
