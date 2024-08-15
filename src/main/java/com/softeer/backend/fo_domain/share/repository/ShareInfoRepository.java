package com.softeer.backend.fo_domain.share.repository;

import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareInfoRepository extends JpaRepository<ShareInfo, Integer> {
    Optional<ShareInfo> findShareInfoByUserId(Integer userId);

    @Query("UPDATE ShareInfo s SET s.remainDrawCount = s.remainDrawCount + 1 WHERE s.userId = :userId")
    void increaseRemainDrawCount(Integer userId);
}
