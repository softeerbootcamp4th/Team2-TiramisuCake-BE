package com.softeer.backend.fo_domain.comment.repository;

import com.softeer.backend.fo_domain.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 기대평 entity repository 클래스
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> findAllByIdLessThanOrderByIdDesc(Integer id, Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM comment WHERE id IN (SELECT id FROM comment ORDER BY id ASC LIMIT 5000)", nativeQuery = true)
    void deleteOldComments();

    long count();
}
