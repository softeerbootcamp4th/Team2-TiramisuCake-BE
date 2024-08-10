package com.softeer.backend.fo_domain.draw.repository;

import com.softeer.backend.fo_domain.draw.domain.Draw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrawRepository extends JpaRepository<Draw, Integer> {

    @Query("SELECT d FROM Draw d JOIN FETCH d.user WHERE d.rank = :rank")
    List<Draw> findDrawWithUser(@Param("rank") int rank);
}
