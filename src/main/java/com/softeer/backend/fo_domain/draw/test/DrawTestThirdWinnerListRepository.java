package com.softeer.backend.fo_domain.draw.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrawTestThirdWinnerListRepository extends JpaRepository<DrawTestThirdWinnerList, Integer> {
    boolean existsByUserId(Integer userId);

    long count();
}
