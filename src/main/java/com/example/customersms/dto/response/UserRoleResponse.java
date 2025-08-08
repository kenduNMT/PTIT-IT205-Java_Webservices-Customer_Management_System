package com.example.customersms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponse {

    // User information
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String userStatus;

    // Role information
    private Long roleId;
    private String roleName;
    private String roleDescription;

    // Timestamp information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}