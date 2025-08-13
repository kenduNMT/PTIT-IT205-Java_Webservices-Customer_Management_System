package com.example.customersms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponse {

    @JsonProperty("purchase_id")
    private Integer purchaseId;

    @JsonProperty("customer_id")
    private Integer customerId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonProperty("purchase_date")
    private LocalDateTime purchaseDate;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    private String currency;

    @JsonProperty("payment_method")
    private String paymentMethod;

    private String status;

    private String notes;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}