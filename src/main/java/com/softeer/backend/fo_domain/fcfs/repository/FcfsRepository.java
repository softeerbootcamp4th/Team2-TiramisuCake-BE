package com.softeer.backend.fo_domain.fcfs.repository;

import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcfsRepository extends JpaRepository<Fcfs, Integer> {

}
