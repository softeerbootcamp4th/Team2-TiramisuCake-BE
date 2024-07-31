package com.softeer.backend.fo_domain.share.repository;

import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareInfoRepository extends JpaRepository<ShareInfo, Integer> {
    String findSharedUrlByUserId(Integer userId);
}
