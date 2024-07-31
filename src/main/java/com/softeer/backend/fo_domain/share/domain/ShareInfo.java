package com.softeer.backend.fo_domain.share.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "share_info")
public class ShareInfo {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "shared_url")
    private String sharedUrl;

    @Column(name = "invited_num")
    private Integer invitedNum;

    @Column(name = "draw_remain_cnt")
    private Integer drawRemainCnt;

    @Builder
    public ShareInfo(Integer userId, String sharedUrl, Integer invitedNum, Integer drawRemainCnt) {
        this.userId = userId;
        this.sharedUrl = sharedUrl;
        this.invitedNum = invitedNum;
        this.drawRemainCnt = drawRemainCnt;
    }
}
