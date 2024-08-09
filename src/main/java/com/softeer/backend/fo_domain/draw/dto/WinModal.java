package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WinModal {
    private String title; // 제목
    private String subtitle; // 부제목
    private String img; // 이미지 URL (S3 URL)
    private String description; // 설명

    @Builder
    public WinModal(String title, String subtitle, String img, String description) {
        this.title = title;
        this.subtitle = subtitle;
        this.img = img;
        this.description = description;
    }
}
