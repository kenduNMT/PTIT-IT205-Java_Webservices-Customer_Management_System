package com.example.customersms.dto.request;

import com.example.customersms.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @NotBlank(message = "Thành phố không được để trống")
    @Size(max = 100, message = "Tên thành phố không được vượt quá 100 ký tự")
    private String city;

    @NotBlank(message = "Quốc gia không được để trống")
    @Size(max = 100, message = "Tên quốc gia không được vượt quá 100 ký tự")
    private String country;

    private Customer.Status status = Customer.Status.ACTIVE;

    private Long groupId;
}