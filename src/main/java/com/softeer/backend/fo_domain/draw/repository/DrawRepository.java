package com.softeer.backend.fo_domain.draw.repository;

import com.softeer.backend.fo_domain.draw.domain.Draw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawRepository extends JpaRepository<Draw, Integer> {
}
