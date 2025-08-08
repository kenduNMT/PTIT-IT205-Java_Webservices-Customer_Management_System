package com.example.customersms.service;

import com.example.customersms.dto.request.AssignRoleRequest;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.dto.response.UserRoleResponse;
import org.springframework.data.domain.Pageable;

public interface UserRoleService {

    /**
     * Lấy danh sách user roles với phân trang và filter
     */
    PageResponse<UserRoleResponse> getUserRoles(Pageable pageable, String keyword, String roleName);

    /**
     * Lấy thông tin user role theo userId và roleId
     */
    UserRoleResponse getUserRoleByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * Phân quyền cho user
     */
    UserRoleResponse assignRole(AssignRoleRequest request);

    /**
     * Cập nhật phân quyền của user (thay thế role cũ bằng role mới)
     */
    UserRoleResponse updateUserRole(Long userId, Long oldRoleId, Long newRoleId);

    /**
     * Thu hồi quyền của user
     */
    void revokeRole(Long userId, Long roleId);

    /**
     * Lấy tất cả roles của một user
     */
    PageResponse<UserRoleResponse> getUserRolesByUserId(Long userId, Pageable pageable);

    /**
     * Lấy tất cả users có một role cụ thể
     */
    PageResponse<UserRoleResponse> getUsersByRoleId(Long roleId, Pageable pageable);
}