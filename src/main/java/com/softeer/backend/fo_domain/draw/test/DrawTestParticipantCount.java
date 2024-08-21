package com.softeer.backend.fo_domain.draw.test;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "draw_test_participant_count")
public class DrawTestParticipantCount {
    @Id
    @Column(name = "participant_count_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer participantCountId;

    @Column(name = "count")
    Integer count;
}
