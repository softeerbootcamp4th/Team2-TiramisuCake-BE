package com.softeer.backend.fo_domain.fcfs.domain;

import com.softeer.backend.fo_domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
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

    @Column(name = "winning_date")
    private LocalDateTime winningDate;

}
