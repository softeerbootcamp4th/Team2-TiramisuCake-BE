package com.softeer.backend.fo_domain.share.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
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
}
