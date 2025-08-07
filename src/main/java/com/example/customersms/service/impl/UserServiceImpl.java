package com.example.customersms.service.impl;

import com.example.customersms.dto.request.ChangePasswordRequest;
import com.example.customersms.dto.request.UpdateProfileRequest;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.dto.response.UserResponse;
import com.example.customersms.entity.User;
import com.example.customersms.mapper.UserMapper;
import com.example.customersms.repository.UserRepository;
import com.example.customersms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse getProfile(Long userId) {
        log.info("Lấy thông tin profile cho user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra user có bị soft delete không
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("Người dùng đã bị xóa");
        }

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("Cập nhật profile cho user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra user có bị soft delete không
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("Người dùng đã bị xóa");
        }

        // Kiểm tra email đã được sử dụng bởi user khác chưa
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
        }

        // Cập nhật thông tin
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        User updatedUser = userRepository.save(user);
        log.info("Cập nhật profile thành công cho user: {}", updatedUser.getUsername());

        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Thay đổi mật khẩu cho user ID: {}", userId);

        // Kiểm tra xác nhận mật khẩu
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra user có bị soft delete không
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("Người dùng đã bị xóa");
        }

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Thay đổi mật khẩu thành công cho user: {}", user.getUsername());
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(Pageable pageable, String keyword) {
        log.info("Lấy danh sách users với keyword: {}, page: {}", keyword, pageable.getPageNumber());

        Page<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Chỉ lấy users không bị soft delete
            users = userRepository.findByKeywordAndStatusNot(keyword.trim(), User.UserStatus.DELETED, pageable);
        } else {
            // Chỉ lấy users không bị soft delete
            users = userRepository.findByStatusNot(User.UserStatus.DELETED, pageable);
        }

        // Chuyển đổi Page<User> thành PageResponse<UserResponse>
        Page<UserResponse> userResponsePage = users.map(userMapper::toUserResponse);

        return convertToPageResponse(userResponsePage);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long userId, User.UserStatus status) {
        log.info("Cập nhật trạng thái user ID: {} thành {}", userId, status);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra user đã bị soft delete chưa
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("Không thể thay đổi trạng thái của người dùng đã bị xóa");
        }

        user.setStatus(status);
        User updatedUser = userRepository.save(user);

        log.info("Cập nhật trạng thái thành công cho user: {}", updatedUser.getUsername());

        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Soft delete user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra user đã bị soft delete chưa
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("Người dùng đã được xóa trước đó");
        }

        // Kiểm tra user có role ADMIN không
        if (hasAdminRole(user)) {
            throw new RuntimeException("Không thể xóa người dùng có quyền Admin");
        }

        // Thực hiện soft delete bằng cách thay đổi status
        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);

        log.info("Soft delete thành công user: {}", user.getUsername());
    }

    @Override
    public Long getUserIdByUsername(String username) {
        log.debug("Lấy user ID cho username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra user có bị soft delete không
        if (user.getStatus() == User.UserStatus.DELETED) {
            throw new RuntimeException("Người dùng đã bị xóa");
        }

        return user.getId();
    }

    // Helper method để chuyển đổi Page sang PageResponse
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

    // Helper method để kiểm tra user có role ADMIN không
    private boolean hasAdminRole(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        return user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equalsIgnoreCase(String.valueOf(role.getName())) ||
                        "ROLE_ADMIN".equalsIgnoreCase(String.valueOf(role.getName())));
    }
}