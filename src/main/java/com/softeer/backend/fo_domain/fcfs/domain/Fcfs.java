package com.softeer.backend.fo_domain.fcfs.domain;

import com.softeer.backend.fo_domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 선착순 당첨 정보를 관리하는 entity 클래스
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "fcfs")
public class Fcfs {

    @Id
    @Column(name = "fcfs_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "round")
    private int round;

    @Column(name = "code")
    private String code;

    @Column(name = "winning_date", nullable = false)
    private LocalDate winningDate;

}
