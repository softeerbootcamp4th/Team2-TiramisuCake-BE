package com.softeer.backend.fo_domain.fcfs.repository;

import com.softeer.backend.fo_domain.fcfs.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 퀴즈 entity repository 클래스
 */
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
}
