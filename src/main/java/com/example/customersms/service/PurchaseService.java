package com.example.customersms.service;

import com.example.customersms.dto.request.PurchaseRequest;
import com.example.customersms.dto.request.PurchaseUpdateRequest;
import com.example.customersms.dto.response.PurchaseResponse;
import com.example.customersms.entity.Purchase.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PurchaseService {

    PurchaseResponse createPurchase(PurchaseRequest request);

    PurchaseResponse updatePurchase(Integer purchaseId, PurchaseUpdateRequest request);

    void deletePurchase(Integer purchaseId);

    PurchaseResponse getPurchaseById(Integer purchaseId);

    Page<PurchaseResponse> getAllPurchases(Pageable pageable);

    Page<PurchaseResponse> getPurchasesByCustomerId(Long customerId, Pageable pageable);

    Page<PurchaseResponse> searchPurchases(Long customerId,
                                           PurchaseStatus status,
                                           LocalDateTime startDate,
                                           LocalDateTime endDate,
                                           BigDecimal minAmount,
                                           BigDecimal maxAmount,
                                           String paymentMethod,
                                           Pageable pageable);

    BigDecimal getTotalAmountByCustomerId(Long customerId);

    Long countPurchasesByStatus(PurchaseStatus status);

    PurchaseResponse updatePurchaseStatus(Integer purchaseId, PurchaseStatus status);

    boolean customerHasPurchases(Long customerId);
}
