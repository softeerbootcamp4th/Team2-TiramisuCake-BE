package com.softeer.backend.fo_domain.share.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "share_url_info")
public class ShareUrlInfo {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "share_url")
    private String shareUrl;
}
