package com.softeer.backend.fo_domain.fcfs.repository;

import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcfsRepository extends JpaRepository<Fcfs, Integer> {

    @Query("SELECT f FROM Fcfs f JOIN FETCH f.user WHERE f.round = :round")
    List<Fcfs> findFcfsWithUser(@Param("round") int round);

}
