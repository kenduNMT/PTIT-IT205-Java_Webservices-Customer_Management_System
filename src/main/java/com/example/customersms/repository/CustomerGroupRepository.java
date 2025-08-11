package com.example.customersms.repository;

import com.example.customersms.entity.CustomerGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Long> {

    // Kiểm tra tên nhóm đã tồn tại
    boolean existsByGroupNameIgnoreCase(String groupName);

    // Kiểm tra tên nhóm đã tồn tại (loại trừ ID hiện tại)
    boolean existsByGroupNameIgnoreCaseAndIdNot(String groupName, Long id);

    // Tìm kiếm theo tên hoặc mô tả
    Page<CustomerGroup> findByGroupNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String groupName, String description, Pageable pageable);
}