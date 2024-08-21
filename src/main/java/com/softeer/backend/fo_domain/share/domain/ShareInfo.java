package com.softeer.backend.fo_domain.share.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공유 정보 엔티티 클래스
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "share_info")
public class ShareInfo {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "invited_num")
    private Integer invitedNum;

    @Column(name = "remain_draw_count")
    private Integer remainDrawCount;

    @Builder
    public ShareInfo(Integer userId, Integer invitedNum, Integer remainDrawCount) {
        this.userId = userId;
        this.invitedNum = invitedNum;
        this.remainDrawCount = remainDrawCount;
    }
}
