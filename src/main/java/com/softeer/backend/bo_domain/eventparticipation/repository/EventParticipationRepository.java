package com.softeer.backend.bo_domain.eventparticipation.repository;

import com.softeer.backend.bo_domain.eventparticipation.domain.EventParticipation;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Integer> {

    default EventParticipation findSingleEventParticipation() {
        List<EventParticipation> results = findAll();
        if (results.isEmpty()) {
            throw new EmptyResultDataAccessException("Entity not found", 1);
        }
        return results.get(0);
    }
}
