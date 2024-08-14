package com.softeer.backend.fo_domain.fcfs.repository;

import com.softeer.backend.fo_domain.fcfs.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
}
