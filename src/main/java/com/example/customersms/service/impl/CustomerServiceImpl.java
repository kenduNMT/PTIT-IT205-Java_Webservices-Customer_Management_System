package com.example.customersms.service.impl;

import com.example.customersms.dto.request.*;
import com.example.customersms.dto.response.ApiResponse;
import com.example.customersms.dto.response.CustomerProfileResponse;
import com.example.customersms.dto.response.CustomerResponse;
import com.example.customersms.dto.response.PageResponse;
import com.example.customersms.entity.Customer;
import com.example.customersms.entity.CustomerGroup;
import com.example.customersms.entity.User;
import com.example.customersms.repository.CustomerGroupRepository;
import com.example.customersms.repository.CustomerRepository;
import com.example.customersms.repository.UserRepository;
import com.example.customersms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerGroupRepository customerGroupRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // ============ ADMIN METHODS ============
    @Override
    public PageResponse<CustomerResponse> getAllCustomers(
            int page, int size, String sortBy, String sortDir,
            Customer.Status status, String city, String country, Long groupId) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Customer> customerPage = customerRepository.findCustomersWithFilters(
                status, city, country, groupId, pageable);

        List<CustomerResponse> content = customerPage.getContent()
                .stream()
                .map(this::convertToCustomerResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.isFirst(),
                customerPage.isLast()
        );
    }

    @Override
    public ApiResponse<CustomerResponse> createCustomer(CustomerCreateRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + request.getUserId()));

            if (customerRepository.existsByUserId(request.getUserId())) {
                return ApiResponse.error("User này đã có customer profile");
            }

            CustomerGroup customerGroup = null;
            if (request.getGroupId() != null) {
                customerGroup = customerGroupRepository.findById(request.getGroupId())
                        .orElseThrow(() -> new RuntimeException("Customer Group không tồn tại với ID: " + request.getGroupId()));
            }

            Customer customer = Customer.builder()
                    .user(user)
                    .address(request.getAddress())
                    .city(request.getCity())
                    .country(request.getCountry())
                    .status(request.getStatus())
                    .customerGroup(customerGroup)
                    .build();

            Customer savedCustomer = customerRepository.save(customer);
            CustomerResponse response = convertToCustomerResponse(savedCustomer);

            return ApiResponse.success("Tạo customer thành công", response);

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi tạo customer: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<CustomerResponse> updateCustomer(Long customerId, CustomerUpdateRequest request) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer không tồn tại với ID: " + customerId));

            if (request.getAddress() != null) {
                customer.setAddress(request.getAddress());
            }
            if (request.getCity() != null) {
                customer.setCity(request.getCity());
            }
            if (request.getCountry() != null) {
                customer.setCountry(request.getCountry());
            }
            if (request.getStatus() != null) {
                customer.setStatus(request.getStatus());
            }
            if (request.getGroupId() != null) {
                CustomerGroup customerGroup = customerGroupRepository.findById(request.getGroupId())
                        .orElseThrow(() -> new RuntimeException("Customer Group không tồn tại với ID: " + request.getGroupId()));
                customer.setCustomerGroup(customerGroup);
            }

            Customer updatedCustomer = customerRepository.save(customer);
            CustomerResponse response = convertToCustomerResponse(updatedCustomer);

            return ApiResponse.success("Cập nhật customer thành công", response);

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi cập nhật customer: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<CustomerResponse> getCustomerById(Long customerId) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer không tồn tại với ID: " + customerId));

            CustomerResponse response = convertToCustomerResponse(customer);
            return ApiResponse.success("Lấy thông tin customer thành công", response);

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi lấy thông tin customer: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> deleteCustomer(Long customerId) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer không tồn tại với ID: " + customerId));

            customer.setStatus(Customer.Status.BLOCKED);
            customerRepository.save(customer);

            return ApiResponse.success("Xóa customer thành công", "Customer đã được chuyển sang trạng thái BLOCKED");

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi xóa customer: " + e.getMessage());
        }
    }

    // ============ CUSTOMER METHODS ============
    @Override
    public ApiResponse<CustomerProfileResponse> getCustomerProfile(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));

            Customer customer = customerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Customer profile không tồn tại"));

            CustomerProfileResponse response = convertToCustomerProfileResponse(customer);
            return ApiResponse.success("Lấy thông tin profile thành công", response);

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi lấy thông tin profile: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<CustomerProfileResponse> updateCustomerProfile(String username, CustomerProfileUpdateRequest request) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));

            Customer customer = customerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Customer profile không tồn tại"));

            if (request.getAddress() != null) {
                customer.setAddress(request.getAddress());
            }
            if (request.getCity() != null) {
                customer.setCity(request.getCity());
            }
            if (request.getCountry() != null) {
                customer.setCountry(request.getCountry());
            }

            Customer updatedCustomer = customerRepository.save(customer);
            CustomerProfileResponse response = convertToCustomerProfileResponse(updatedCustomer);

            return ApiResponse.success("Cập nhật profile thành công", response);

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi cập nhật profile: " + e.getMessage());
        }
    }

    // ============ STAFF METHODS ============
    @Override
    public ApiResponse<CustomerResponse> updateCustomerStatus(Long customerId, Customer.Status newStatus) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer không tồn tại với ID: " + customerId));

            customer.setStatus(newStatus);
            Customer updatedCustomer = customerRepository.save(customer);
            CustomerResponse response = convertToCustomerResponse(updatedCustomer);

            return ApiResponse.success("Cập nhật trạng thái customer thành công", response);

        } catch (Exception e) {
            return ApiResponse.error("Lỗi khi cập nhật trạng thái customer: " + e.getMessage());
        }
    }

    // ============ HELPER METHODS ============
    private CustomerResponse convertToCustomerResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setAddress(customer.getAddress());
        response.setCity(customer.getCity());
        response.setCountry(customer.getCountry());
        response.setStatus(customer.getStatus());
        response.setCreatedAt(customer.getCreatedAt().format(formatter));
        response.setUpdatedAt(customer.getUpdatedAt().format(formatter));

        if (customer.getUser() != null) {
            response.setUserId(customer.getUser().getId());
            response.setUsername(customer.getUser().getUsername());
            response.setEmail(customer.getUser().getEmail());
            response.setFullName(customer.getUser().getFullName());
        }

        if (customer.getCustomerGroup() != null) {
            response.setGroupId(customer.getCustomerGroup().getId());
            response.setGroupName(customer.getCustomerGroup().getGroupName());
        }

        return response;
    }

    private CustomerProfileResponse convertToCustomerProfileResponse(Customer customer) {
        CustomerProfileResponse response = new CustomerProfileResponse();
        response.setId(customer.getId());
        response.setAddress(customer.getAddress());
        response.setCity(customer.getCity());
        response.setCountry(customer.getCountry());
        response.setStatus(customer.getStatus());
        response.setCreatedAt(customer.getCreatedAt().format(formatter));
        response.setUpdatedAt(customer.getUpdatedAt().format(formatter));

        if (customer.getCustomerGroup() != null) {
            response.setGroupName(customer.getCustomerGroup().getGroupName());
        }

        return response;
    }
}
