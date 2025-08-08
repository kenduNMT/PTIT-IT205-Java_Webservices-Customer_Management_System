package com.example.customersms.controller;

import com.example.customersms.dto.request.AssignRoleRequest;
import com.example.customersms.dto.response.ApiResponse;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.dto.response.UserRoleResponse;
import com.example.customersms.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user-roles")
@Slf4j
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    /**
     * Lấy danh sách phân quyền (phân trang, filter có keyword, roleName)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserRoleResponse>>> getUserRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleName,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            log.info("Lấy danh sách user roles - page: {}, size: {}, keyword: {}, role: {}",
                    page, size, keyword, roleName);

            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            PageResponse<UserRoleResponse> result = userRoleService.getUserRoles(pageable, keyword, roleName);

            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách phân quyền thành công", result));

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách user roles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Lấy thông tin chi tiết phân quyền theo userId và roleId
     */
    @GetMapping("/user/{userId}/role/{roleId}")
    public ResponseEntity<ApiResponse<UserRoleResponse>> getUserRoleByUserIdAndRoleId(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        try {
            log.info("Lấy thông tin user role với userId: {}, roleId: {}", userId, roleId);

            UserRoleResponse result = userRoleService.getUserRoleByUserIdAndRoleId(userId, roleId);

            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin phân quyền thành công", result));

        } catch (RuntimeException e) {
            log.error("Lỗi khi lấy thông tin user role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi lấy thông tin user role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Lấy tất cả roles của một user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<UserRoleResponse>>> getUserRolesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Lấy danh sách roles của user ID: {}", userId);

            Pageable pageable = PageRequest.of(page, size);
            PageResponse<UserRoleResponse> result = userRoleService.getUserRolesByUserId(userId, pageable);

            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách roles của user thành công", result));

        } catch (RuntimeException e) {
            log.error("Lỗi khi lấy roles của user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Lấy tất cả users có một role cụ thể
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<ApiResponse<PageResponse<UserRoleResponse>>> getUsersByRoleId(
            @PathVariable Long roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Lấy danh sách users có role ID: {}", roleId);

            Pageable pageable = PageRequest.of(page, size);
            PageResponse<UserRoleResponse> result = userRoleService.getUsersByRoleId(roleId, pageable);

            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách users có role thành công", result));

        } catch (RuntimeException e) {
            log.error("Lỗi khi lấy users có role: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Phân quyền cho user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserRoleResponse>> assignRole(@Valid @RequestBody AssignRoleRequest request) {
        try {
            log.info("Phân quyền cho user ID: {} với role ID: {}", request.getUserId(), request.getRoleId());

            UserRoleResponse result = userRoleService.assignRole(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Phân quyền thành công", result));

        } catch (RuntimeException e) {
            log.error("Lỗi khi phân quyền: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi phân quyền: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật phân quyền của user (thay thế role cũ bằng role mới)
     */
    @PutMapping("/user/{userId}/role/{oldRoleId}")
    public ResponseEntity<ApiResponse<UserRoleResponse>> updateUserRole(
            @PathVariable Long userId,
            @PathVariable Long oldRoleId,
            @Valid @RequestBody AssignRoleRequest request) {
        try {
            log.info("Cập nhật phân quyền cho user ID: {} từ role {} sang role {}",
                    userId, oldRoleId, request.getRoleId());

            // Đảm bảo userId trong path và request body khớp nhau
            if (!userId.equals(request.getUserId())) {
                throw new RuntimeException("User ID trong URL và request body không khớp");
            }

            UserRoleResponse result = userRoleService.updateUserRole(userId, oldRoleId, request.getRoleId());

            return ResponseEntity.ok(ApiResponse.success("Cập nhật phân quyền thành công", result));

        } catch (RuntimeException e) {
            log.error("Lỗi khi cập nhật phân quyền: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi cập nhật phân quyền: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Thu hồi quyền của user
     */
    @DeleteMapping("/user/{userId}/role/{roleId}")
    public ResponseEntity<ApiResponse<String>> revokeRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        try {
            log.info("Thu hồi quyền userId: {}, roleId: {}", userId, roleId);

            userRoleService.revokeRole(userId, roleId);

            return ResponseEntity.ok(ApiResponse.success("Thu hồi quyền thành công",
                    "Đã xóa phân quyền userId: " + userId + ", roleId: " + roleId));

        } catch (RuntimeException e) {
            log.error("Lỗi khi thu hồi quyền: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi thu hồi quyền: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }
}