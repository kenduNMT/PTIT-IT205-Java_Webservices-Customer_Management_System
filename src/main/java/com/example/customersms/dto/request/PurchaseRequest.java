package com.example.customersms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    @NotNull(message = "Customer ID không được để trống")
    @Positive(message = "Customer ID phải là số dương")
    private Integer customerId;

    @NotNull(message = "Tổng số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tổng số tiền phải lớn hơn 0")
    @Digits(integer = 13, fraction = 2, message = "Tổng số tiền không đúng định dạng")
    private BigDecimal totalAmount;

    @NotBlank(message = "Đơn vị tiền tệ không được để trống")
    @Size(max = 10, message = "Đơn vị tiền tệ không được vượt quá 10 ký tự")
    private String currency = "VND";

    @Size(max = 50, message = "Phương thức thanh toán không được vượt quá 50 ký tự")
    private String paymentMethod;

    @Pattern(regexp = "PENDING|COMPLETED|CANCELLED",
            message = "Trạng thái phải là PENDING, COMPLETED hoặc CANCELLED")
    private String status = "PENDING";

    private String notes;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime purchaseDate;
}