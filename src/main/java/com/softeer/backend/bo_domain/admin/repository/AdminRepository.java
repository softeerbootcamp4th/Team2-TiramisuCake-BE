package com.softeer.backend.bo_domain.admin.repository;

import com.softeer.backend.bo_domain.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByAccount(String account);

    boolean existsByAccount(String account);
}
