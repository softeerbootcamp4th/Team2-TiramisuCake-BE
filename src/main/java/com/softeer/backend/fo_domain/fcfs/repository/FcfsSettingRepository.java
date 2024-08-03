package com.softeer.backend.fo_domain.fcfs.repository;

import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcfsSettingRepository extends JpaRepository<FcfsSetting, Integer> {

    Optional<FcfsSetting> findByRound(int round);

}
