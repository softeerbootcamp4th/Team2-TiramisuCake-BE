package com.softeer.backend.fo_domain.draw.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "draw_test_second_winner_list")
public class DrawTestSecondWinnerList {
    @Id
    @Column(name = "user_id")
    Integer userId;
}
