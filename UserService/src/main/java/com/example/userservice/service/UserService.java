package com.example.userservice.service;

import com.example.userservice.dto.LoginRequest;
import com.example.userservice.dto.LoginResponse;
import com.example.userservice.dto.OrderAssignmentRequest;
import com.example.userservice.dto.RegisterRequest;
import com.example.userservice.model.User;
import com.example.userservice.model.UserOrder;
import com.example.userservice.repository.UserOrderRepository;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOrderRepository userOrderRepository;

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return new LoginResponse(false, "User not found");
        }

        User user = userOpt.get();

        // In production, use BCrypt password encoder
        if (!user.getPassword().equals(request.getPassword())) {
            return new LoginResponse(false, "Invalid password");
        }

        LoginResponse response = new LoginResponse(true, "Login successful");
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return new LoginResponse(false, "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new LoginResponse(false, "Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // In production, hash this
        user.setEmail(request.getEmail());

        user = userRepository.save(user);

        LoginResponse response = new LoginResponse(true, "Registration successful");
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }

    @Transactional
    public UserOrder assignOrderToUser(OrderAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);
        userOrder.setOrderId(request.getOrderId());
        userOrder.setTotalAmount(request.getTotalAmount());

        return userOrderRepository.save(userOrder);
    }

    public List<UserOrder> getUserOrders(Long userId) {
        return userOrderRepository.findByUser_UserId(userId);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
