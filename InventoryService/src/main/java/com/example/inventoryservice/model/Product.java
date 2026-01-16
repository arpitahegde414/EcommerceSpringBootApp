package com.example.inventoryservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "product_name", nullable = false)
    private String productName;
}
