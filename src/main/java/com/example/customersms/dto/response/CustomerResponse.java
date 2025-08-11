package com.example.customersms.dto.response;

import com.example.customersms.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String address;
    private String city;
    private String country;
    private Customer.Status status;
    private String createdAt;
    private String updatedAt;

    // Thông tin user
    private Long userId;
    private String username;
    private String email;
    private String fullName;

    // Thông tin group
    private Long groupId;
    private String groupName;
}