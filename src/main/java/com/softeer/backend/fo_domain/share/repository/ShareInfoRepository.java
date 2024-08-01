package com.softeer.backend.fo_domain.share.repository;

import com.softeer.backend.fo_domain.share.domain.ShareInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShareInfoRepository extends JpaRepository<ShareInfo, Integer> {
    Optional<String> findSharedUrlByUserId(Integer userId);
}
