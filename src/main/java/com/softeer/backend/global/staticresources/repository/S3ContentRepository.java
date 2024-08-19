package com.softeer.backend.global.staticresources.repository;

import com.softeer.backend.global.staticresources.domain.S3Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3ContentRepository extends JpaRepository<S3Content, Integer> {
}

