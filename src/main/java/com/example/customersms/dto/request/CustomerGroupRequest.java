package com.example.customersms.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerGroupRequest {

    @NotBlank(message = "Tên nhóm khách hàng không được để trống")
    @Size(min = 2, max = 100, message = "Tên nhóm khách hàng phải từ 2 đến 100 ký tự")
    private String groupName;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @Builder.Default
    private Boolean isActive = true;
}