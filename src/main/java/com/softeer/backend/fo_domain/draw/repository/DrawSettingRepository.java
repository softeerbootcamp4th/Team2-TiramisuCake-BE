package com.softeer.backend.fo_domain.draw.repository;

import com.softeer.backend.fo_domain.draw.domain.DrawSetting;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Table(name = "draw_setting")
public interface DrawSettingRepository extends JpaRepository<DrawSetting, Integer> {
}
