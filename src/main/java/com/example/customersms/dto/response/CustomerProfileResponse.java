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
public class CustomerProfileResponse {
    private Long id;
    private String address;
    private String city;
    private String country;
    private Customer.Status status;
    private String createdAt;
    private String updatedAt;
    private String groupName;
}