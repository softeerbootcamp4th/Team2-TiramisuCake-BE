package com.softeer.backend.fo_domain.fcfs.service.test;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface FcfsCountRepository extends JpaRepository<FcfsCount, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<FcfsCount> findByRound(int round);
}
