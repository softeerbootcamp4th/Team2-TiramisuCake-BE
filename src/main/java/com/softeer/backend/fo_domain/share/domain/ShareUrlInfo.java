package com.softeer.backend.fo_domain.share.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 공유 url 정보 엔티티 클래스
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "share_url_info")
public class ShareUrlInfo {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "share_url")
    private String shareUrl;
}
