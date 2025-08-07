package com.example.customersms.controller;

import com.example.customersms.dto.request.ChangePasswordRequest;
import com.example.customersms.dto.request.UpdateProfileRequest;
import com.example.customersms.dto.request.UpdateUserStatusRequest;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.dto.response.UserResponse;
import com.example.customersms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    // ===== ADMIN ENDPOINTS =====

    @GetMapping("/admin/users")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        log.info("Admin lấy danh sách users - page: {}, size: {}, keyword: {}", page, size, keyword);

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserResponse> users = userService.getAllUsers(pageable, keyword);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Admin lấy thông tin user ID: {}", id);

        UserResponse user = userService.getProfile(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<UserResponse> updateUserByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {

        log.info("Admin cập nhật thông tin user ID: {}", id);

        UserResponse updatedUser = userService.updateProfile(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/admin/users/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request) {

        log.info("Admin cập nhật trạng thái user ID: {} thành {}", id, request.getStatus());

        UserResponse updatedUser = userService.updateUserStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Admin xóa user ID: {}", id);

        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // ===== STAFF ENDPOINTS =====

    @GetMapping("/STAFF/users")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsersForStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        log.info("Staff lấy danh sách users - page: {}, size: {}, keyword: {}", page, size, keyword);

        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserResponse> users = userService.getAllUsers(pageable, keyword);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/STAFF/users/{id}")
    public ResponseEntity<UserResponse> getUserByIdForStaff(@PathVariable Long id) {
        log.info("Staff lấy thông tin user ID: {}", id);

        UserResponse user = userService.getProfile(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/STAFF/users/{id}")
    public ResponseEntity<UserResponse> updateUserByStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {

        log.info("Staff cập nhật thông tin user ID: {}", id);

        UserResponse updatedUser = userService.updateProfile(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    // ===== PROFILE ENDPOINTS (Authenticated users) =====

    @GetMapping("/auth/profile")
    public ResponseEntity<UserResponse> getMyProfile() {
        Long currentUserId = getCurrentUserId();
        log.info("User lấy profile của chính mình - ID: {}", currentUserId);

        UserResponse user = userService.getProfile(currentUserId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/auth/profile")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        Long currentUserId = getCurrentUserId();
        log.info("User cập nhật profile của chính mình - ID: {}", currentUserId);

        UserResponse updatedUser = userService.updateProfile(currentUserId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/auth/profile/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        Long currentUserId = getCurrentUserId();
        log.info("User thay đổi mật khẩu - ID: {}", currentUserId);

        userService.changePassword(currentUserId, request);
        return ResponseEntity.ok().build();
    }

    // Helper method để lấy ID của user hiện tại
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserIdByUsername(username);
    }
}