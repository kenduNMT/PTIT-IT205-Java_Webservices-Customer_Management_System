package com.example.customersms.service;

import com.example.customersms.dto.request.ChangePasswordRequest;
import com.example.customersms.dto.request.UpdateProfileRequest;
import com.example.customersms.dto.response.UserResponse;

public interface UserService {
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
}