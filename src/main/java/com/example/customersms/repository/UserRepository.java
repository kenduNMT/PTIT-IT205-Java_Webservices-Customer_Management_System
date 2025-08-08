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

    /**
     * Tìm users có role cụ thể
     */
    Page<User> findByRoles_Id(Long roleId, Pageable pageable);

    /**
     * Đếm số users có role cụ thể
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersWithRole(@Param("roleId") Long roleId);

    /**
     * Tìm tất cả users có roles (với phân trang)
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    Page<User> findAllWithRoles(Pageable pageable);

    /**
     * Tìm users với filter keyword và roleName
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles r WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:roleName IS NULL OR :roleName = '' OR " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :roleName, '%')))")
    Page<User> findUsersWithRoleFilter(@Param("keyword") String keyword,
                                       @Param("roleName") String roleName,
                                       Pageable pageable);
}