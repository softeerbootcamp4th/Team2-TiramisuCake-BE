package com.softeer.backend.fo_domain.share.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "share_info")
public class ShareInfo {
    @Id
    @Column(name = "share_info_id")
    private Integer shareInfoId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "shared_url")
    private String sharedUrl;

    @Column(name = "invited_num")
    private Integer invitedNum;

    @Column(name = "draw_remain_cnt")
    private Integer drawRemainCnt;
}
