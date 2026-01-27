package com.example.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private Long userId;
    private String username;
    private String email;
    private String message;
    private boolean success;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
