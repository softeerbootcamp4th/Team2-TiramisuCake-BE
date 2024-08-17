package com.softeer.backend.fo_domain.draw.dto.modal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WinModal {
    private String title; // 제목
    private String subtitle; // 부제목
    private String img; // 이미지 URL (S3 URL)
    private String description; // 설명
}
