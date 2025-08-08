package com.example.customersms.service.impl;

import com.example.customersms.dto.request.AssignRoleRequest;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.dto.response.UserRoleResponse;
import com.example.customersms.entity.Roles;
import com.example.customersms.entity.User;
import com.example.customersms.repository.RoleRepository;
import com.example.customersms.repository.UserRepository;
import com.example.customersms.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public PageResponse<UserRoleResponse> getUserRoles(Pageable pageable, String keyword, String roleName) {
        log.info("Lấy danh sách user roles với keyword: {}, roleName: {}, page: {}",
                keyword, roleName, pageable.getPageNumber());

        // Query users với filter
        Page<User> users = getUsersWithFilter(keyword, roleName, pageable);

        // Chuyển đổi thành UserRoleResponse
        List<UserRoleResponse> userRoleResponses = new ArrayList<>();

        for (User user : users.getContent()) {
            Set<Roles> userRoles = user.getRoles();
            if (userRoles != null && !userRoles.isEmpty()) {
                for (Roles role : userRoles) {
                    // Filter theo roleName nếu có
                    if (roleName == null || roleName.trim().isEmpty() ||
                            role.getName().toString().toLowerCase().contains(roleName.trim().toLowerCase())) {

                        UserRoleResponse response = UserRoleResponse.builder()
                                .userId(user.getId())
                                .username(user.getUsername())
                                .fullName(user.getFullName())
                                .email(user.getEmail())
                                .roleId(role.getId())
                                .roleName(role.getName().toString())
                                .roleDescription(role.getDescription())
                                .userStatus(user.getStatus().toString())
                                .createdAt(user.getCreatedAt())
                                .updatedAt(user.getUpdatedAt())
                                .build();

                        userRoleResponses.add(response);
                    }
                }
            }
        }

        // Tạo Page từ List
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userRoleResponses.size());
        List<UserRoleResponse> pageContent = userRoleResponses.subList(start, end);

        Page<UserRoleResponse> page = new PageImpl<>(pageContent, pageable, userRoleResponses.size());

        return convertToPageResponse(page);
    }

    @Override
    public UserRoleResponse getUserRoleByUserIdAndRoleId(Long userId, Long roleId) {
        log.info("Lấy thông tin user role với userId: {}, roleId: {}", userId, roleId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + roleId));

        // Kiểm tra user có role này không
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getId().equals(roleId));

        if (!hasRole) {
            throw new RuntimeException("Người dùng không có vai trò này");
        }

        return UserRoleResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roleId(role.getId())
                .roleName(role.getName().toString())
                .roleDescription(role.getDescription())
                .userStatus(user.getStatus().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserRoleResponse assignRole(AssignRoleRequest request) {
        log.info("Phân quyền cho user ID: {} với role ID: {}", request.getUserId(), request.getRoleId());

        // Kiểm tra user tồn tại
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + request.getUserId()));

        // Kiểm tra user có bị xóa không
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("Không thể phân quyền cho người dùng đã bị xóa");
        }

        // Kiểm tra role tồn tại
        Roles role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + request.getRoleId()));

        // Kiểm tra user đã có role này chưa
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getId().equals(request.getRoleId()));

        if (hasRole) {
            throw new RuntimeException("Người dùng đã có vai trò này");
        }

        // Thêm role cho user
        user.getRoles().add(role);
        User savedUser = userRepository.save(user);

        log.info("Phân quyền thành công cho user: {} với role: {}", user.getUsername(), role.getName().toString());

        return UserRoleResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .roleId(role.getId())
                .roleName(role.getName().toString())
                .roleDescription(role.getDescription())
                .userStatus(savedUser.getStatus().toString())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserRoleResponse updateUserRole(Long userId, Long oldRoleId, Long newRoleId) {
        log.info("Cập nhật phân quyền userId: {} từ roleId: {} sang roleId: {}", userId, oldRoleId, newRoleId);

        // Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        // Kiểm tra user có bị xóa không
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("Không thể cập nhật phân quyền cho người dùng đã bị xóa");
        }

        // Kiểm tra old role tồn tại
        Roles oldRole = roleRepository.findById(oldRoleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò cũ với ID: " + oldRoleId));

        // Kiểm tra new role tồn tại
        Roles newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò mới với ID: " + newRoleId));

        // Kiểm tra user có old role không
        boolean hasOldRole = user.getRoles().stream()
                .anyMatch(r -> r.getId().equals(oldRoleId));

        if (!hasOldRole) {
            throw new RuntimeException("Người dùng không có vai trò cũ để cập nhật");
        }

        // Kiểm tra user đã có new role chưa
        boolean hasNewRole = user.getRoles().stream()
                .anyMatch(r -> r.getId().equals(newRoleId));

        if (hasNewRole) {
            throw new RuntimeException("Người dùng đã có vai trò mới này");
        }

        // Kiểm tra không xóa role ADMIN cuối cùng
        if (oldRole.getName() == Roles.RoleName.ROLE_ADMIN) {
            long adminCount = userRepository.countUsersWithRole(oldRoleId);
            if (adminCount <= 1) {
                throw new RuntimeException("Không thể thay đổi vai trò Admin cuối cùng");
            }
        }

        // Xóa old role và thêm new role
        user.getRoles().removeIf(r -> r.getId().equals(oldRoleId));
        user.getRoles().add(newRole);

        User savedUser = userRepository.save(user);

        log.info("Cập nhật phân quyền thành công cho user: {} từ role: {} sang role: {}",
                user.getUsername(), oldRole.getName().toString(), newRole.getName().toString());

        return UserRoleResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .roleId(newRole.getId())
                .roleName(newRole.getName().toString())
                .roleDescription(newRole.getDescription())
                .userStatus(savedUser.getStatus().toString())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void revokeRole(Long userId, Long roleId) {
        log.info("Thu hồi quyền userId: {}, roleId: {}", userId, roleId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + roleId));

        // Kiểm tra user có role này không
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getId().equals(roleId));

        if (!hasRole) {
            throw new RuntimeException("Người dùng không có vai trò này");
        }

        // Kiểm tra không xóa role ADMIN cuối cùng
        if (role.getName() == Roles.RoleName.ROLE_ADMIN) {
            long adminCount = userRepository.countUsersWithRole(roleId);
            if (adminCount <= 1) {
                throw new RuntimeException("Không thể thu hồi quyền Admin cuối cùng");
            }
        }

        // Xóa role khỏi user
        user.getRoles().removeIf(r -> r.getId().equals(roleId));
        userRepository.save(user);

        log.info("Thu hồi quyền thành công cho user: {} với role: {}", user.getUsername(), role.getName().toString());
    }

    @Override
    public PageResponse<UserRoleResponse> getUserRolesByUserId(Long userId, Pageable pageable) {
        log.info("Lấy danh sách roles của user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        List<UserRoleResponse> responses = user.getRoles().stream()
                .map(role -> UserRoleResponse.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .roleId(role.getId())
                        .roleName(role.getName().toString())
                        .roleDescription(role.getDescription())
                        .userStatus(user.getStatus().toString())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        // Tạo Page từ List
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<UserRoleResponse> pageContent = responses.subList(start, end);

        Page<UserRoleResponse> page = new PageImpl<>(pageContent, pageable, responses.size());

        return convertToPageResponse(page);
    }

    @Override
    public PageResponse<UserRoleResponse> getUsersByRoleId(Long roleId, Pageable pageable) {
        log.info("Lấy danh sách users có role ID: {}", roleId);

        Roles role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + roleId));

        Page<User> users = userRepository.findByRoles_Id(roleId, pageable);

        Page<UserRoleResponse> responses = users.map(user ->
                UserRoleResponse.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .roleId(role.getId())
                        .roleName(role.getName().toString())
                        .roleDescription(role.getDescription())
                        .userStatus(user.getStatus().toString())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build()
        );

        return convertToPageResponse(responses);
    }

    // Helper methods
    private Page<User> getUsersWithFilter(String keyword, String roleName, Pageable pageable) {
        if ((keyword != null && !keyword.trim().isEmpty()) || (roleName != null && !roleName.trim().isEmpty())) {
            return userRepository.findUsersWithRoleFilter(keyword, roleName, pageable);
        } else {
            return userRepository.findAllWithRoles(pageable);
        }
    }

    private <T> PageResponse<T> convertToPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}