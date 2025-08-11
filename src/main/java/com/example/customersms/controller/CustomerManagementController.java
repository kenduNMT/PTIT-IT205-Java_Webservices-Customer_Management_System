package com.example.customersms.controller;

import com.example.customersms.dto.request.*;
import com.example.customersms.dto.response.ApiResponse;
import com.example.customersms.dto.response.CustomerProfileResponse;
import com.example.customersms.dto.response.CustomerResponse;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.entity.Customer;
import com.example.customersms.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CustomerManagementController {

    @Autowired
    private CustomerService customerService;

    // ==================== ADMIN ====================

    @GetMapping("/admin/customers")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> getAllCustomersForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Customer.Status status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Long groupId) {

        PageResponse<CustomerResponse> customers = customerService.getAllCustomers(
                page, size, sortBy, sortDir, status, city, country, groupId);

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách customers thành công", customers));
    }

    @PostMapping("/admin/customers")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerCreateRequest request) {

        ApiResponse<CustomerResponse> response = customerService.createCustomer(request);

        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/admin/customers/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByIdForAdmin(@PathVariable Long id) {
        return getCustomerByIdCommon(id);
    }

    @PutMapping("/admin/customers/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {

        ApiResponse<CustomerResponse> response = customerService.updateCustomer(id, request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/admin/customers/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable Long id) {
        ApiResponse<String> response = customerService.deleteCustomer(id);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // ==================== CUSTOMER ====================

    @GetMapping("/customer/profile")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        ApiResponse<CustomerProfileResponse> response = customerService.getCustomerProfile(username);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/customer/profile")
    public ResponseEntity<ApiResponse<CustomerProfileResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileUpdateRequest request) {

        String username = authentication.getName();
        ApiResponse<CustomerProfileResponse> response = customerService.updateCustomerProfile(username, request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // ==================== STAFF ====================

    @GetMapping("/staff/customers")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> getCustomersForStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Customer.Status status) {

        PageResponse<CustomerResponse> customers = customerService.getAllCustomers(
                page, size, sortBy, sortDir, status, null, null, null);

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách customers thành công", customers));
    }

    @GetMapping("/staff/customers/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByIdForStaff(@PathVariable Long id) {
        return getCustomerByIdCommon(id);
    }

    @PutMapping("/staff/customers/{id}/status")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomerStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {

        ApiResponse<CustomerResponse> response = customerService.updateCustomerStatus(id, request.getStatus());
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // ==================== COMMON METHOD ====================

    private ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByIdCommon(Long id) {
        ApiResponse<CustomerResponse> response = customerService.getCustomerById(id);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

}
