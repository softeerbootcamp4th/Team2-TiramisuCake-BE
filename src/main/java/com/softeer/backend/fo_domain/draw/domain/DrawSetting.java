package com.softeer.backend.fo_domain.draw.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 추첨 이벤트 설정 엔티티 클래스
 */
@Entity
@Getter
@Setter
@Builder
@Table(name = "draw_setting")
@NoArgsConstructor
@AllArgsConstructor
public class DrawSetting {
    @Id
    @Column(name = "draw_setting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer draw_setting_id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "winner_num_1")
    private Integer winnerNum1;

    @Column(name = "winner_num_2")
    private Integer winnerNum2;

    @Column(name = "winner_num_3")
    private Integer winnerNum3;
}
