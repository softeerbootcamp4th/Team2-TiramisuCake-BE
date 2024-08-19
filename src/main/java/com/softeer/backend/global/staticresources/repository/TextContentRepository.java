package com.softeer.backend.global.staticresources.repository;

import com.softeer.backend.global.staticresources.domain.TextContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextContentRepository extends JpaRepository<TextContent, Integer> {
}
