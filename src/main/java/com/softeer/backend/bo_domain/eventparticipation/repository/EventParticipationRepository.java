package com.softeer.backend.bo_domain.eventparticipation.repository;

import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 이벤트 참가자를 관리하는 entity repository 클래스
 */
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Integer> {

    /**
     * 시작날짜, 종료날짜에 해당하는 EventParticipation 엔티티 조회
     */
    @Query("SELECT e FROM EventParticipation e WHERE e.eventDate BETWEEN :startDate AND :endDate ORDER BY e.eventDate ASC")
    List<EventParticipation> findAllByEventDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
