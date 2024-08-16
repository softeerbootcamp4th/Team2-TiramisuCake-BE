package com.softeer.backend.bo_domain.eventparticipation.repository;

import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Integer> {

    @Query("SELECT e FROM EventParticipation e WHERE e.eventDate BETWEEN :startDate AND :endDate")
    List<EventParticipation> findAllByEventDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    default EventParticipation findSingleEventParticipation() {
        List<EventParticipation> results = findAll();
        if (results.isEmpty()) {
            throw new EmptyResultDataAccessException("Entity not found", 1);
        }
        return results.get(0);
    }
}
