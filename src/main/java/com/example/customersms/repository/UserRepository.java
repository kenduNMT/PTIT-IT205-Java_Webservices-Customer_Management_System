package com.example.customersms.repository;

import com.example.customersms.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Tìm kiếm với từ khóa và loại trừ users bị soft delete
    @Query("SELECT u FROM User u WHERE " +
            "u.status != :excludeStatus AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> findByKeywordAndStatusNot(@Param("keyword") String keyword,
                                         @Param("excludeStatus") User.UserStatus excludeStatus,
                                         Pageable pageable);

    // Lấy tất cả users trừ những user có status cụ thể (thường là DELETED)
    Page<User> findByStatusNot(User.UserStatus excludeStatus, Pageable pageable);

    // Method gốc cho tìm kiếm (có thể giữ lại để backward compatibility)
    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}