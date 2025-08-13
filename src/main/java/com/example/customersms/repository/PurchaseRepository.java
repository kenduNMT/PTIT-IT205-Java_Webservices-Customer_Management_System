package com.example.customersms.repository;

import com.example.customersms.entity.Purchase;
import com.example.customersms.entity.Purchase.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    // Tìm kiếm giao dịch theo customer ID
    Page<Purchase> findByCustomer_Id(Long customerId, Pageable pageable);

    // Tìm kiếm tổng hợp với nhiều điều kiện
    @Query("SELECT p FROM Purchase p WHERE " +
            "(:customerId IS NULL OR p.customer.id = :customerId) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:startDate IS NULL OR p.purchaseDate >= :startDate) AND " +
            "(:endDate IS NULL OR p.purchaseDate <= :endDate) AND " +
            "(:minAmount IS NULL OR p.totalAmount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR p.totalAmount <= :maxAmount) AND " +
            "(:paymentMethod IS NULL OR LOWER(p.paymentMethod) LIKE LOWER(CONCAT('%', :paymentMethod, '%'))) " +
            "ORDER BY p.purchaseDate DESC")
    Page<Purchase> findPurchasesWithFilters(@Param("customerId") Long customerId,
                                            @Param("status") PurchaseStatus status,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            @Param("minAmount") BigDecimal minAmount,
                                            @Param("maxAmount") BigDecimal maxAmount,
                                            @Param("paymentMethod") String paymentMethod,
                                            Pageable pageable);

    // Thống kê tổng số tiền theo customer
    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.customer.id = :customerId AND p.status = 'COMPLETED'")
    Optional<BigDecimal> getTotalAmountByCustomerId(@Param("customerId") Long customerId);

    // Thống kê số lượng giao dịch theo trạng thái
    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.status = :status")
    Long countByStatus(@Param("status") PurchaseStatus status);

    // Kiểm tra customer có giao dịch hay không
    boolean existsByCustomer_Id(Long customerId);

    // Tìm giao dịch theo ID với thông tin customer
    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.customer WHERE p.purchaseId = :purchaseId")
    Optional<Purchase> findByIdWithCustomer(@Param("purchaseId") Integer purchaseId);

    // Lấy danh sách giao dịch với thông tin customer
    @Query("SELECT p FROM Purchase p LEFT JOIN FETCH p.customer ORDER BY p.purchaseDate DESC")
    Page<Purchase> findAllWithCustomer(Pageable pageable);
}
