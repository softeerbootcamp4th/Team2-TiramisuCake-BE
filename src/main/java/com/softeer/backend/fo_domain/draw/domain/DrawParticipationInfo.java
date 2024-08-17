package com.softeer.backend.fo_domain.draw.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "draw_participation_info")
public class DrawParticipationInfo {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "draw_winning_count")
    private Integer drawWinningCount;

    @Column(name = "draw_losing_count")
    private Integer drawLosingCount;

    @Column(name = "draw_attendance_count")
    private Integer drawAttendanceCount;

    @Column(name = "last_attendance")
    private LocalDateTime lastAttendance;
}
