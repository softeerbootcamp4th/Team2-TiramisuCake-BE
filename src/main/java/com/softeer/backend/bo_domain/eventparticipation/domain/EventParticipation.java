package com.softeer.backend.bo_domain.eventparticipation.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private int visitorCount = 0;

    @Column(name = "fcfs_participant_count", nullable = false)
    @Builder.Default
    private int fcfsParticipantCount = 0;

    @Column(name = "draw_participant_count", nullable = false)
    @Builder.Default
    private int drawParticipantCount = 0;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

}
