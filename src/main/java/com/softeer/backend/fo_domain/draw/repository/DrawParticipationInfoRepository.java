package com.softeer.backend.fo_domain.draw.repository;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrawParticipationInfoRepository extends JpaRepository<DrawParticipationInfo, Integer> {
    Optional<DrawParticipationInfo> findDrawParticipationInfoByUserId(Integer userId);
}
