package com.softeer.backend.fo_domain.draw.domain;

import com.softeer.backend.fo_domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "draw")
@AllArgsConstructor
@Builder
public class Draw {
    @Id
    @Column(name = "draw_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer drawId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "draw_rank")
    private Integer rank;

    @Column(name = "winning_date")
    private Date winningDate;
}
