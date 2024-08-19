package com.softeer.backend.fo_domain.share.repository;

import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ShareInfoRepository extends JpaRepository<ShareInfo, Integer> {
    Optional<ShareInfo> findShareInfoByUserId(Integer userId);

    @Modifying
    @Query("UPDATE ShareInfo s SET s.invitedNum = s.invitedNum + 1, s.remainDrawCount = s.remainDrawCount + 1 WHERE s.userId = :userId")
    void increaseInvitedNumAndRemainDrawCount(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE ShareInfo  s SET s.remainDrawCount = s.remainDrawCount - 1 WHERE s.userId = :userId")
    void decreaseRemainDrawCount(Integer userId);
}
