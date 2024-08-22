package com.softeer.backend.fo_domain.draw.test;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrawTestThirdWinnerListRepository extends JpaRepository<DrawTestThirdWinnerList, Integer> {
    boolean existsByUserId(Integer userId);

    long count();

    @Modifying
    @Query(value = "LOCK TABLES test_database.draw_test_third_winner_list WRITE", nativeQuery = true)
    void lockThirdWinnerTable();

    @Modifying
    @Query(value = "UNLOCK TABLES", nativeQuery = true)
    void unlockThirdWinnerTable();

    // 또는 JPA 엔티티에 대해 비관적 잠금을 직접 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DrawTestThirdWinnerList d")
    List<DrawTestThirdWinnerList> findAllForUpdate();
}
