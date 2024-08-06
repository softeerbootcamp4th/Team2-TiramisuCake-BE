package com.softeer.backend.fo_domain.draw.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "draw")
public class Draw {
    @Id
    @Column(name = "draw_id")
    private Integer drawId;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "draw_rank")
    private Integer rank;

    @Column(name = "winning_date")
    private Date winningDate;

    @Builder
    public Draw(Integer drawId, Integer userId, Integer rank, Date winningDate) {
        this.drawId = drawId;
        this.userId = userId;
        this.rank = rank;
        this.winningDate = winningDate;
    }
}
