package com.softeer.backend.fo_domain.draw.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

@Entity
@Getter
@Table(name = "draw_setting")
@RequiredArgsConstructor
public class DrawSetting {
    @Id
    @Column(name = "draw_setting_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer draw_setting_id;

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Column(name = "end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Column(name = "winner_num_1")
    private Integer winnerNum1;

    @Column(name = "winner_num_2")
    private Integer winnerNum2;

    @Column(name = "winner_num_3")
    private Integer winnerNum3;
}
