package com.softeer.backend.global.staticresources.repository;

import com.softeer.backend.global.staticresources.domain.TextContent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TextContent entity의 repository 클래스
 */
public interface TextContentRepository extends JpaRepository<TextContent, Integer> {
}
