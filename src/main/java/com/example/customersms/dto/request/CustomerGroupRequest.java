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

    @DecimalMin(value = "0.0", inclusive = true, message = "Phần trăm giảm giá phải >= 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Phần trăm giảm giá phải <= 100")
    private Double discountPercentage;

    @Min(value = 1, message = "Mức độ ưu tiên phải >= 1")
    @Max(value = 10, message = "Mức độ ưu tiên phải <= 10")
    private Integer priorityLevel;

    @Builder.Default
    private Boolean isActive = true;
}