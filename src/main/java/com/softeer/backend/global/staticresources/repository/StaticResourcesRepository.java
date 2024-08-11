package com.softeer.backend.global.staticresources.repository;

import com.softeer.backend.global.staticresources.domain.StaticResources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticResourcesRepository extends JpaRepository<StaticResources, Integer> {
}
