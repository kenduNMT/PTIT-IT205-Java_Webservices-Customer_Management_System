package com.example.customersms.repository;

import com.example.customersms.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByName(Roles.RoleName name);

    boolean existsByName(Roles.RoleName name);
}