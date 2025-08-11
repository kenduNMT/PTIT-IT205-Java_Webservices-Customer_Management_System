package com.example.customersms.dto.request;

import com.example.customersms.entity.Customer;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateRequest {

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Size(max = 100, message = "Tên thành phố không được vượt quá 100 ký tự")
    private String city;

    @Size(max = 100, message = "Tên quốc gia không được vượt quá 100 ký tự")
    private String country;

    private Customer.Status status;

    private Long groupId;
}