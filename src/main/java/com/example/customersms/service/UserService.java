package com.example.customersms.service;

import com.example.customersms.dto.request.ChangePasswordRequest;
import com.example.customersms.dto.request.UpdateProfileRequest;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.dto.response.UserResponse;
import com.example.customersms.entity.User;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);

    // Thêm các method mới
    PageResponse<UserResponse> getAllUsers(Pageable pageable, String keyword);
    UserResponse updateUserStatus(Long userId, User.UserStatus status);
    void deleteUser(Long userId);
    Long getUserIdByUsername(String username);
}