package com.softeer.backend.bo_domain.eventparticipation.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "event_participation")
public class EventParticipation {

    @Id
    @Column(name = "event_participation_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "total_visitors_count", nullable = false)
    @Builder.Default
    private int totalVisitorsCount = 0;

    @Column(name = "fcfs_participant_count", nullable = false)
    @Builder.Default
    private int fcfsParticipantCount = 0;

    @Column(name = "draw_participant_count", nullable = false)
    @Builder.Default
    private int drawParticipantCount = 0;

    public void addTotalVisitorsCount(int totalVisitorsCount) {
        this.totalVisitorsCount += totalVisitorsCount;
    }

    public void addFcfsParticipantCount(int fcfsParticipantCount) {
        this.fcfsParticipantCount += fcfsParticipantCount;
    }

    public void addDrawParticipantCount(int drawParticipantCount) {
        this.drawParticipantCount += drawParticipantCount;
    }
}
