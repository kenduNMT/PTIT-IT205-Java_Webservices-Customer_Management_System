package com.example.customersms.repository;

import com.example.customersms.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Tìm customer theo user ID
    Optional<Customer> findByUserId(Long userId);

    // Tìm customer theo nhiều tiêu chí với phân trang
    @Query("SELECT c FROM Customer c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:city IS NULL OR c.city LIKE %:city%) AND " +
            "(:country IS NULL OR c.country LIKE %:country%) AND " +
            "(:groupId IS NULL OR c.customerGroup.id = :groupId)")
    Page<Customer> findCustomersWithFilters(
            @Param("status") Customer.Status status,
            @Param("city") String city,
            @Param("country") String country,
            @Param("groupId") Long groupId,
            Pageable pageable
    );

    // Kiểm tra customer có tồn tại theo user ID không
    boolean existsByUserId(Long userId);
}