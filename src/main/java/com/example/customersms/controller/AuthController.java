package com.example.customersms.controller;

import com.example.customersms.config.security.principal.UserDetailsCus;
import com.example.customersms.dto.request.ChangePasswordRequest;
import com.example.customersms.dto.request.LoginRequest;
import com.example.customersms.dto.request.RegisterRequest;
import com.example.customersms.dto.request.UpdateProfileRequest;
import com.example.customersms.dto.response.ApiResponse;
import com.example.customersms.dto.response.AuthResponse;
import com.example.customersms.dto.response.UserResponse;
import com.example.customersms.service.AuthService;
import com.example.customersms.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;
    /**
     * API đăng nhập
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest,
                                                           BindingResult bindingResult) {
        log.info("Yêu cầu đăng nhập từ username: {}", loginRequest.getUsername());

        // Validate input
        if (bindingResult.hasErrors()) {
            List<ApiResponse.ErrorDetail> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new ApiResponse.ErrorDetail(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
        }

        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", authResponse));
        } catch (Exception e) {
            log.error("Lỗi đăng nhập: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không chính xác"));
        }
    }

    /**
     * API đăng ký tài khoản mới
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest,
                                                              BindingResult bindingResult) {
        log.info("Yêu cầu đăng ký tài khoản mới với username: {}", registerRequest.getUsername());

        // Validate input
        if (bindingResult.hasErrors()) {
            List<ApiResponse.ErrorDetail> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new ApiResponse.ErrorDetail(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
        }

        try {
            UserResponse userResponse = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đăng ký tài khoản thành công", userResponse));
        } catch (RuntimeException e) {
            log.error("Lỗi đăng ký: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi đăng ký: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Đã xảy ra lỗi hệ thống"));
        }
    }

    /**
     * API xác thực email (có thể mở rộng sau)
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        // TODO: Implement email verification logic
        return ResponseEntity.ok(ApiResponse.success("Xác thực email thành công", null));
    }

    /**
     * Lấy thông tin profile của user hiện tại
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile() {
        try {
            Long userId = getCurrentUserId();
            UserResponse userResponse = userService.getProfile(userId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin profile thành công", userResponse));
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không thể lấy thông tin profile: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật thông tin profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            BindingResult bindingResult) {

        // Validate input
        if (bindingResult.hasErrors()) {
            List<ApiResponse.ErrorDetail> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new ApiResponse.ErrorDetail(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
        }

        try {
            Long userId = getCurrentUserId();
            UserResponse userResponse = userService.updateProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công", userResponse));
        } catch (RuntimeException e) {
            log.error("Lỗi cập nhật profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi cập nhật profile: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Đã xảy ra lỗi hệ thống"));
        }
    }

    /**
     * Thay đổi mật khẩu
     */
    @PutMapping("/profile/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            BindingResult bindingResult) {

        // Validate input
        if (bindingResult.hasErrors()) {
            List<ApiResponse.ErrorDetail> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new ApiResponse.ErrorDetail(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
        }

        try {
            Long userId = getCurrentUserId();
            userService.changePassword(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Thay đổi mật khẩu thành công", null));
        } catch (RuntimeException e) {
            log.error("Lỗi thay đổi mật khẩu: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi thay đổi mật khẩu: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Đã xảy ra lỗi hệ thống"));
        }
    }

    /**
     * Helper method để lấy ID của user hiện tại từ JWT token
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsCus) {
            UserDetailsCus userDetails = (UserDetailsCus) authentication.getPrincipal();
            return userDetails.getId();
        }
        throw new RuntimeException("Không thể xác định người dùng hiện tại");
    }
}