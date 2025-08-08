package com.example.customersms.repository;

import com.example.customersms.entity.CustomerGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Long> {

    // Tìm theo tên nhóm (không phân biệt hoa thường)
    Optional<CustomerGroup> findByGroupNameIgnoreCase(String groupName);

    // Kiểm tra tên nhóm đã tồn tại
    boolean existsByGroupNameIgnoreCase(String groupName);

    // Kiểm tra tên nhóm đã tồn tại (loại trừ ID hiện tại)
    boolean existsByGroupNameIgnoreCaseAndIdNot(String groupName, Long id);

    // Tìm kiếm theo tên hoặc mô tả
    Page<CustomerGroup> findByGroupNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String groupName, String description, Pageable pageable);

    // Tìm tất cả nhóm đang hoạt động
    List<CustomerGroup> findByIsActiveTrue();

    // Tìm theo trạng thái
    Page<CustomerGroup> findByIsActive(Boolean isActive, Pageable pageable);

    // Tìm theo mức độ ưu tiên
    List<CustomerGroup> findByPriorityLevelOrderByPriorityLevelAsc(Integer priorityLevel);

    // Tìm nhóm có giảm giá trong khoảng
    @Query("SELECT cg FROM CustomerGroup cg WHERE cg.discountPercentage BETWEEN :minDiscount AND :maxDiscount")
    List<CustomerGroup> findByDiscountPercentageBetween(
            @Param("minDiscount") Double minDiscount,
            @Param("maxDiscount") Double maxDiscount);

    // Đếm số lượng khách hàng trong mỗi nhóm
    @Query("SELECT cg.id, COUNT(c.id) FROM CustomerGroup cg LEFT JOIN cg.customers c GROUP BY cg.id")
    List<Object[]> countCustomersByGroup();

    // Tìm các nhóm có ít nhất một khách hàng
    @Query("SELECT DISTINCT cg FROM CustomerGroup cg WHERE SIZE(cg.customers) > 0")
    List<CustomerGroup> findGroupsWithCustomers();

    // Tìm các nhóm không có khách hàng nào
    @Query("SELECT cg FROM CustomerGroup cg WHERE SIZE(cg.customers) = 0")
    List<CustomerGroup> findGroupsWithoutCustomers();
}