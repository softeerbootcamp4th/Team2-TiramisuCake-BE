package com.softeer.backend.fo_domain.user.repository;

import com.softeer.backend.fo_domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
