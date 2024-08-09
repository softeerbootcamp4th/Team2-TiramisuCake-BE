package com.softeer.backend.fo_domain.share.repository;

import com.softeer.backend.fo_domain.share.domain.ShareUrlInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareUrlInfoRepository extends JpaRepository<ShareUrlInfo, Integer> {
    @Query("SELECT s.shareUrl FROM ShareUrlInfo s WHERE s.userId = :userId")
    Optional<String> findShareUrlByUserId(Integer userId);
}
