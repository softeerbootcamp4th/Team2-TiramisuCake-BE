package com.softeer.backend.fo_domain.share.repository;

import com.softeer.backend.fo_domain.share.domain.ShareUrlInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 공유 url 정보 엔티티를 관리하기 위한 레포지토리 클래스
 */
@Repository
public interface ShareUrlInfoRepository extends JpaRepository<ShareUrlInfo, Integer> {
    @Query("SELECT s.shareUrl FROM ShareUrlInfo s WHERE s.userId = :userId")
    Optional<String> findShareUrlByUserId(Integer userId);

    @Query("SELECT s.userId FROM ShareUrlInfo s WHERE s.shareUrl = :shareUrl")
    Optional<Integer> findUserIdByShareUrl(String shareUrl);

    boolean existsByShareUrl(String shareUrl);
}
