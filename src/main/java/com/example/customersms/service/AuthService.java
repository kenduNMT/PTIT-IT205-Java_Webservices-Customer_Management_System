package com.example.customersms.service;

import com.example.customersms.dto.request.LoginRequest;
import com.example.customersms.dto.request.RegisterRequest;
import com.example.customersms.dto.response.AuthResponse;
import com.example.customersms.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    UserResponse register(RegisterRequest registerRequest);
    void initDefaultRoles();
}