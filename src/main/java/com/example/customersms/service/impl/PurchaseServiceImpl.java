package com.example.customersms.service.impl;

import com.example.customersms.dto.request.PurchaseRequest;
import com.example.customersms.dto.request.PurchaseUpdateRequest;
import com.example.customersms.dto.response.PurchaseResponse;
import com.example.customersms.entity.Customer;
import com.example.customersms.entity.Purchase;
import com.example.customersms.entity.Purchase.PurchaseStatus;
import com.example.customersms.repository.CustomerRepository;
import com.example.customersms.repository.PurchaseRepository;
import com.example.customersms.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CustomerRepository customerRepository;

    @Override
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        log.info("Creating purchase for customer ID: {}", request.getCustomerId());

        // Kiểm tra customer có tồn tại không
        Customer customer = customerRepository.findById(Long.valueOf(request.getCustomerId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + request.getCustomerId()));

        // Tạo purchase entity
        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setTotalAmount(request.getTotalAmount());
        purchase.setCurrency(request.getCurrency());
        purchase.setPaymentMethod(request.getPaymentMethod());
        purchase.setStatus(PurchaseStatus.valueOf(request.getStatus()));
        purchase.setNotes(request.getNotes());

        // Nếu có purchaseDate trong request thì dùng, không thì dùng current time
        if (request.getPurchaseDate() != null) {
            purchase.setPurchaseDate(request.getPurchaseDate());
        }

        Purchase savedPurchase = purchaseRepository.save(purchase);
        log.info("Purchase created successfully with ID: {}", savedPurchase.getPurchaseId());

        return mapToPurchaseResponse(savedPurchase);
    }

    @Override
    public PurchaseResponse updatePurchase(Integer purchaseId, PurchaseUpdateRequest request) {
        log.info("Updating purchase with ID: {}", purchaseId);

        Purchase purchase = purchaseRepository.findByIdWithCustomer(purchaseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + purchaseId));

        // Cập nhật các trường nếu có giá trị mới
        if (request.getTotalAmount() != null) {
            purchase.setTotalAmount(request.getTotalAmount());
        }
        if (request.getCurrency() != null && !request.getCurrency().trim().isEmpty()) {
            purchase.setCurrency(request.getCurrency());
        }
        if (request.getPaymentMethod() != null) {
            purchase.setPaymentMethod(request.getPaymentMethod());
        }
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            purchase.setStatus(PurchaseStatus.valueOf(request.getStatus()));
        }
        if (request.getNotes() != null) {
            purchase.setNotes(request.getNotes());
        }
        if (request.getPurchaseDate() != null) {
            purchase.setPurchaseDate(request.getPurchaseDate());
        }

        Purchase updatedPurchase = purchaseRepository.save(purchase);
        log.info("Purchase updated successfully with ID: {}", updatedPurchase.getPurchaseId());

        return mapToPurchaseResponse(updatedPurchase);
    }

    @Override
    public void deletePurchase(Integer purchaseId) {
        log.info("Deleting purchase with ID: {}", purchaseId);

        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + purchaseId));

        purchaseRepository.delete(purchase);
        log.info("Purchase deleted successfully with ID: {}", purchaseId);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse getPurchaseById(Integer purchaseId) {
        log.info("Getting purchase with ID: {}", purchaseId);

        Purchase purchase = purchaseRepository.findByIdWithCustomer(purchaseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + purchaseId));

        return mapToPurchaseResponse(purchase);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> getAllPurchases(Pageable pageable) {
        log.info("Getting all purchases with pagination: {}", pageable);

        Page<Purchase> purchases = purchaseRepository.findAllWithCustomer(pageable);
        return purchases.map(this::mapToPurchaseResponse);
    }

    @Override
    public Page<PurchaseResponse> getPurchasesByCustomerId(Long customerId, Pageable pageable) {
        log.info("Getting purchases for customer ID: {} with pagination: {}", customerId, pageable);

        // Kiểm tra customer có tồn tại không
        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + customerId);
        }

        Page<Purchase> purchases = purchaseRepository.findByCustomer_Id(customerId, pageable);
        return purchases.map(this::mapToPurchaseResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> searchPurchases(Long customerId,
                                                  PurchaseStatus status,
                                                  LocalDateTime startDate,
                                                  LocalDateTime endDate,
                                                  BigDecimal minAmount,
                                                  BigDecimal maxAmount,
                                                  String paymentMethod,
                                                  Pageable pageable) {
        log.info("Searching purchases with filters - Customer: {}, Status: {}, Date range: {} to {}, " +
                        "Amount range: {} to {}, Payment method: {}",
                customerId, status, startDate, endDate, minAmount, maxAmount, paymentMethod);

        Page<Purchase> purchases = purchaseRepository.findPurchasesWithFilters(
                customerId, status, startDate, endDate, minAmount, maxAmount, paymentMethod, pageable
        );
        return purchases.map(this::mapToPurchaseResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountByCustomerId(Long customerId) {
        log.info("Getting total amount for customer ID: {}", customerId);

        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException("Không tìm thấy khách hàng với ID: " + customerId);
        }

        return purchaseRepository.getTotalAmountByCustomerId(customerId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPurchasesByStatus(PurchaseStatus status) {
        log.info("Counting purchases by status: {}", status);
        return purchaseRepository.countByStatus(status);
    }

    @Override
    public PurchaseResponse updatePurchaseStatus(Integer purchaseId, PurchaseStatus status) {
        log.info("Updating purchase status for ID: {} to status: {}", purchaseId, status);

        Purchase purchase = purchaseRepository.findByIdWithCustomer(purchaseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + purchaseId));

        purchase.setStatus(status);
        Purchase updatedPurchase = purchaseRepository.save(purchase);

        log.info("Purchase status updated successfully for ID: {}", purchaseId);
        return mapToPurchaseResponse(updatedPurchase);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean customerHasPurchases(Long customerId) {
        log.info("Checking if customer {} has purchases", customerId);
        return purchaseRepository.existsByCustomer_Id(customerId);
    }

    /**
     * Map Purchase entity to PurchaseResponse DTO
     */
    private PurchaseResponse mapToPurchaseResponse(Purchase purchase) {
        return PurchaseResponse.builder()
                .purchaseId(purchase.getPurchaseId())
                .customerId(Math.toIntExact(purchase.getCustomer().getId()))
                .customerName(purchase.getCustomer().getUser().getFullName())
                .customerEmail(purchase.getCustomer().getUser().getEmail())
                .purchaseDate(purchase.getPurchaseDate())
                .totalAmount(purchase.getTotalAmount())
                .currency(purchase.getCurrency())
                .paymentMethod(purchase.getPaymentMethod())
                .status(purchase.getStatus().name())
                .notes(purchase.getNotes())
                .updatedAt(purchase.getUpdatedAt())
                .build();
    }
}