package com.softeer.backend.fo_domain.fcfs.repository;

import com.softeer.backend.fo_domain.fcfs.domain.FcfsSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcfsSettingRepository extends JpaRepository<FcfsSetting, Integer> {

    Optional<FcfsSetting> findByRound(int round);

}
