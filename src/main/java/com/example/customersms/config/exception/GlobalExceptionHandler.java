package com.example.customersms.config.exception;

import com.example.customersms.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.error("Validation error: {}", ex.getMessage());

        List<ApiResponse.ErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiResponse.ErrorDetail(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Dữ liệu không hợp lệ", errors));
    }

    /**
     * Xử lý lỗi xác thực
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex) {

        log.error("Authentication error: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Tên đăng nhập hoặc mật khẩu không chính xác"));
    }

    /**
     * Xử lý lỗi phân quyền
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex) {

        log.error("Access denied: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Bạn không có quyền truy cập tài nguyên này"));
    }

    /**
     * Xử lý lỗi RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex) {

        log.error("Runtime error: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        log.error("Illegal argument error: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Tham số không hợp lệ: " + ex.getMessage()));
    }

    /**
     * Xử lý các lỗi không được xử lý cụ thể
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau"));
    }

    /**
     * Xử lý lỗi custom - UserNotFoundException
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFoundException(
            UserNotFoundException ex) {

        log.error("User not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Không tìm thấy người dùng"));
    }

    /**
     * Xử lý lỗi custom - DuplicateResourceException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(
            DuplicateResourceException ex) {

        log.error("Duplicate resource: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }
}