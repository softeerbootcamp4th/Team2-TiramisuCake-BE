package com.softeer.backend.fo_domain.fcfs.repository;

import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 선착순 설정 정보 entity repository 클래스
 */
@Repository
public interface FcfsSettingRepository extends JpaRepository<FcfsSetting, Integer> {
}
