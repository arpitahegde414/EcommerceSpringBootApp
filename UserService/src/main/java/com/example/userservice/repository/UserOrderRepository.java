package com.example.userservice.repository;

import com.example.userservice.model.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
    List<UserOrder> findByUser_UserId(Long userId);
    List<UserOrder> findByOrderId(Long orderId);
}
