package com.example.customersms.controller;

import com.example.customersms.dto.request.LoginRequest;
import com.example.customersms.dto.request.RegisterRequest;
import com.example.customersms.dto.response.ApiResponse;
import com.example.customersms.dto.response.AuthResponse;
import com.example.customersms.dto.response.UserResponse;
import com.example.customersms.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

//    API xác thực email (có thể mở rộng sau)
//    @PostMapping("/verify")
//    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
//        // TODO: Implement email verification logic
//        return ResponseEntity.ok(ApiResponse.success("Xác thực email thành công", null));
//
}