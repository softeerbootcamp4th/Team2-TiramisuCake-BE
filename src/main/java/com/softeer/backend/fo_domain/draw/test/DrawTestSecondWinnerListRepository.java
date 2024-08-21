package com.softeer.backend.fo_domain.draw.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawTestSecondWinnerListRepository extends JpaRepository<DrawTestSecondWinnerList, Integer> {
    boolean existsByUserId(Integer userId);

    long count();
}
