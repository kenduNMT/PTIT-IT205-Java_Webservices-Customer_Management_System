package com.example.customersms.controller;

import com.example.customersms.dto.request.PurchaseRequest;
import com.example.customersms.dto.request.PurchaseUpdateRequest;
import com.example.customersms.dto.response.PurchaseResponse;
import com.example.customersms.entity.Purchase.PurchaseStatus;
import com.example.customersms.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * Tạo mới giao dịch mua hàng - STAFF và ADMIN
     */
    @PostMapping("/purchases")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        log.info("Request to create purchase: {}", request);

        try {
            PurchaseResponse response = purchaseService.createPurchase(request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Tạo giao dịch mua hàng thành công");
            result.put("data", response);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("Error creating purchase: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi tạo giao dịch: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Cập nhật giao dịch mua hàng - STAFF và ADMIN
     */
    @PutMapping("/purchases/{purchaseId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePurchase(
            @PathVariable Integer purchaseId,
            @Valid @RequestBody PurchaseUpdateRequest request) {
        log.info("Request to update purchase ID: {} with data: {}", purchaseId, request);

        try {
            PurchaseResponse response = purchaseService.updatePurchase(purchaseId, request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Cập nhật giao dịch thành công");
            result.put("data", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error updating purchase: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi cập nhật giao dịch: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Xóa giao dịch mua hàng - CHỈ ADMIN
     */
    @DeleteMapping("/admin/purchases/{purchaseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePurchase(@PathVariable Integer purchaseId) {
        log.info("Request to delete purchase ID: {}", purchaseId);

        try {
            purchaseService.deletePurchase(purchaseId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Xóa giao dịch thành công");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error deleting purchase: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi xóa giao dịch: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Lấy thông tin giao dịch theo ID - STAFF và ADMIN
     */
    @GetMapping("/purchases/{purchaseId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getPurchaseById(@PathVariable Integer purchaseId) {
        log.info("Request to get purchase ID: {}", purchaseId);

        try {
            PurchaseResponse response = purchaseService.getPurchaseById(purchaseId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Lấy thông tin giao dịch thành công");
            result.put("data", response);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting purchase: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi lấy thông tin giao dịch: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Lấy danh sách tất cả giao dịch với phân trang - STAFF và ADMIN
     */
    @GetMapping("/purchases")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Request to get all purchases - page: {}, size: {}, sortBy: {}, sortDir: {}",
                page, size, sortBy, sortDir);

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<PurchaseResponse> purchases = purchaseService.getAllPurchases(pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Lấy danh sách giao dịch thành công");
            result.put("data", purchases.getContent());
            result.put("currentPage", purchases.getNumber());
            result.put("totalItems", purchases.getTotalElements());
            result.put("totalPages", purchases.getTotalPages());
            result.put("pageSize", purchases.getSize());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting all purchases: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi lấy danh sách giao dịch: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Lấy danh sách giao dịch theo customer ID - STAFF và ADMIN
     */
    @GetMapping("/customers/{customerId}/purchases")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getPurchasesByCustomerId(
            @PathVariable Integer customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Request to get purchases for customer ID: {}", customerId);

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<PurchaseResponse> purchases = purchaseService.getPurchasesByCustomerId(Long.valueOf(customerId), pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Lấy danh sách giao dịch của khách hàng thành công");
            result.put("data", purchases.getContent());
            result.put("currentPage", purchases.getNumber());
            result.put("totalItems", purchases.getTotalElements());
            result.put("totalPages", purchases.getTotalPages());
            result.put("pageSize", purchases.getSize());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting purchases by customer ID: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi lấy danh sách giao dịch: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Tìm kiếm giao dịch với bộ lọc - STAFF và ADMIN
     */
    @GetMapping("/purchases/search")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> searchPurchases(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("Request to search purchases with filters");

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            PurchaseStatus purchaseStatus = null;
            if (status != null && !status.trim().isEmpty()) {
                purchaseStatus = PurchaseStatus.valueOf(status.toUpperCase());
            }

            Page<PurchaseResponse> purchases = purchaseService.searchPurchases(
                    Long.valueOf(customerId), purchaseStatus, startDate, endDate,
                    minAmount, maxAmount, paymentMethod, pageable
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Tìm kiếm giao dịch thành công");
            result.put("data", purchases.getContent());
            result.put("currentPage", purchases.getNumber());
            result.put("totalItems", purchases.getTotalElements());
            result.put("totalPages", purchases.getTotalPages());
            result.put("pageSize", purchases.getSize());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error searching purchases: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi tìm kiếm giao dịch: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Cập nhật trạng thái giao dịch - STAFF và ADMIN
     */
    @PatchMapping("/purchases/{purchaseId}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePurchaseStatus(
            @PathVariable Integer purchaseId,
            @RequestParam String status) {
        log.info("Request to update purchase status - ID: {}, Status: {}", purchaseId, status);

        try {
            PurchaseStatus purchaseStatus = PurchaseStatus.valueOf(status.toUpperCase());
            PurchaseResponse response = purchaseService.updatePurchaseStatus(purchaseId, purchaseStatus);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Cập nhật trạng thái giao dịch thành công");
            result.put("data", response);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Trạng thái không hợp lệ. Vui lòng sử dụng: PENDING, COMPLETED, CANCELLED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("Error updating purchase status: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Lấy tổng số tiền đã hoàn thành của customer - STAFF và ADMIN
     */
    @GetMapping("/customers/{customerId}/total-amount")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getTotalAmountByCustomerId(@PathVariable Integer customerId) {
        log.info("Request to get total amount for customer ID: {}", customerId);

        try {
            BigDecimal totalAmount = purchaseService.getTotalAmountByCustomerId(Long.valueOf(customerId));

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Lấy tổng số tiền thành công");
            result.put("customerId", customerId);
            result.put("totalAmount", totalAmount);
            result.put("currency", "VND");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting total amount: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi lấy tổng số tiền: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Thống kê số lượng giao dịch theo trạng thái - STAFF và ADMIN
     */
    @GetMapping("/purchases/statistics/count-by-status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getStatisticsByStatus() {
        log.info("Request to get purchase statistics by status");

        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("pending", purchaseService.countPurchasesByStatus(PurchaseStatus.PENDING));
            statistics.put("completed", purchaseService.countPurchasesByStatus(PurchaseStatus.COMPLETED));
            statistics.put("cancelled", purchaseService.countPurchasesByStatus(PurchaseStatus.CANCELLED));

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Lấy thống kê thành công");
            result.put("data", statistics);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting statistics: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi lấy thống kê: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Kiểm tra customer có giao dịch hay không - STAFF và ADMIN
     */
    @GetMapping("/customers/{customerId}/has-purchases")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> checkCustomerHasPurchases(@PathVariable Integer customerId) {
        log.info("Request to check if customer {} has purchases", customerId);

        try {
            boolean hasPurchases = purchaseService.customerHasPurchases(Long.valueOf(customerId));

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Kiểm tra thành công");
            result.put("customerId", customerId);
            result.put("hasPurchases", hasPurchases);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error checking customer purchases: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Lỗi khi kiểm tra: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}