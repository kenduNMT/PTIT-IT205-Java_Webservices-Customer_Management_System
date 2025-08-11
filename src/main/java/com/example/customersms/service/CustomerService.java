package com.example.customersms.service;

import com.example.customersms.dto.request.*;
import com.example.customersms.dto.response.ApiResponse;
import com.example.customersms.dto.response.CustomerProfileResponse;
import com.example.customersms.dto.response.CustomerResponse;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.entity.Customer;

public interface CustomerService {

    // ============ ADMIN METHODS ============
    PageResponse<CustomerResponse> getAllCustomers(
            int page, int size, String sortBy, String sortDir,
            Customer.Status status, String city, String country, Long groupId);

    ApiResponse<CustomerResponse> createCustomer(CustomerCreateRequest request);

    ApiResponse<CustomerResponse> updateCustomer(Long customerId, CustomerUpdateRequest request);

    ApiResponse<CustomerResponse> getCustomerById(Long customerId);

    ApiResponse<String> deleteCustomer(Long customerId);

    // ============ CUSTOMER METHODS ============
    ApiResponse<CustomerProfileResponse> getCustomerProfile(String username);

    ApiResponse<CustomerProfileResponse> updateCustomerProfile(String username, CustomerProfileUpdateRequest request);

    // ============ STAFF METHODS ============
    ApiResponse<CustomerResponse> updateCustomerStatus(Long customerId, Customer.Status newStatus);
}
