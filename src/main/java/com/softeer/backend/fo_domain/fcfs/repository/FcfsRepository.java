package com.softeer.backend.fo_domain.fcfs.repository;

import com.softeer.backend.fo_domain.fcfs.domain.Fcfs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 선착순 등록 정보 entity repository 클래스
 */
@Repository
public interface FcfsRepository extends JpaRepository<Fcfs, Integer> {

    /**
     * 선착순 이벤트에서 특정 round에서 당참된 선착순 등록 정보를 반환하는 메서드
     * <p>
     * fetch join으로 user정보도 영속성 컨텍스트로 로딩한다.
     */
    @Query("SELECT f FROM Fcfs f JOIN FETCH f.user WHERE f.round = :round")
    List<Fcfs> findFcfsWithUser(@Param("round") int round);

    List<Fcfs> findByUserIdOrderByWinningDateAsc(Integer userId);

    Optional<Fcfs> findByUserIdAndRound(Integer userId, int round);

    Optional<Fcfs> findByCode(String code);

}
