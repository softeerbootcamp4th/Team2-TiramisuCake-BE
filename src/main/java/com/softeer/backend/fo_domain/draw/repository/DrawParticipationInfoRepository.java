package com.softeer.backend.fo_domain.draw.repository;

import com.softeer.backend.fo_domain.draw.domain.DrawParticipationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DrawParticipationInfoRepository extends JpaRepository<DrawParticipationInfo, Integer> {
    Optional<DrawParticipationInfo> findDrawParticipationInfoByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE DrawParticipationInfo d SET d.drawWinningCount = d.drawWinningCount + 1 WHERE d.userId = :userId")
    void increaseWinCount(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE DrawParticipationInfo d SET d.drawLosingCount = d.drawLosingCount + 1 WHERE d.userId = :userId")
    void increaseLoseCount(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE DrawParticipationInfo d SET d.drawAttendanceCount = d.drawAttendanceCount + 1 WHERE d.userId = :userId")
    void increaseAttendanceCount(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE DrawParticipationInfo d SET d.drawAttendanceCount = 1 WHERE d.userId = :userId")
    void setAttendanceCountToOne(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE DrawParticipationInfo d SET d.lastParticipated = :lastAttendance WHERE d.userId = :userId")
    void setLastAttendance(Integer userId, LocalDateTime lastAttendance);
}
