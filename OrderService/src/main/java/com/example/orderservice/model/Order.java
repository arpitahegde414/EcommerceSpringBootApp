package com.example.orderservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name="Order")
public class Order {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="product_id", referencedColumnName = "id")
    private Product product;

    @Column(name="quantity", nullable = false)
    private Integer Quantity;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus Status;

    @Column(name="order_date", nullable = false)
    private LocalDate OrderDate;

    @Column(name = "reserved_batch_ids")
    private String reservedBatchIds; // Comma-separated batch IDs

    public enum OrderStatus {
        PLACED, SHIPPED, DELIVERED, CANCELLED
    }
}
