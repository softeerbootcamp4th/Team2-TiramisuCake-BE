package com.softeer.backend.fo_domain.draw.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DrawTestParticipantCountRepository extends JpaRepository<DrawTestParticipantCount, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE DrawTestParticipantCount p SET p.count = p.count + 1 WHERE p.participantCountId = 1")
    void increaseParticipantCount();
}
