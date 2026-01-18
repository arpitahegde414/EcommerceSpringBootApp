package com.example.orderservice.repository;

import com.example.orderservice.model.Product;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;

@ReadingConverter
public interface ProductRepository extends JpaRepository<Product, Long> {
}
