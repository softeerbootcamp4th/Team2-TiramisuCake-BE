package com.softeer.backend.global.staticresources.repository;

import com.softeer.backend.global.staticresources.domain.S3Content;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * S3Content entity의 repository 클래스
 */
public interface S3ContentRepository extends JpaRepository<S3Content, Integer> {
}

