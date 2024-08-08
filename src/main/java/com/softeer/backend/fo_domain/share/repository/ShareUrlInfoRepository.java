package com.softeer.backend.fo_domain.share.repository;

import com.softeer.backend.fo_domain.share.domain.ShareUrlInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareUrlInfoRepository extends JpaRepository<ShareUrlInfo, Integer> {
    Optional<String> findShareUrlByUserId(Integer userId);
}
