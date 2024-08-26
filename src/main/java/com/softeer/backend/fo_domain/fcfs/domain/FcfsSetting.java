package com.softeer.backend.fo_domain.fcfs.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 선착순 설정정보를 관리하는 entity 클래스
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "fcfs_setting")
public class FcfsSetting {

    @Id
    @Column(name = "fcfs_setting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "round")
    private int round;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "winner_num")
    private int winnerNum;

}
