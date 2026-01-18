package com.example.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="inventory_batch")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id",nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
}
