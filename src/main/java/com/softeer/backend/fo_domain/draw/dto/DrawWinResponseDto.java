package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


public class DrawWinResponseDto extends DrawResponseDto {
    private WinModal winModal;

    @Data
    @Builder
    public static class WinModal {
        private String title; // 제목
        private String subtitle; // 부제목
        private String img; // 이미지 URL (S3 URL)
        private String description; // 설명
    }
}
