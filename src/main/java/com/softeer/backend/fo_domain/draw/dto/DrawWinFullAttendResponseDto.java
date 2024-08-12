package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;

public class DrawWinFullAttendResponseDto extends DrawWinResponseDto {
    private FullAttendModal fullAttendModal;

    @Data
    @Builder
    public static class FullAttendModal {
        private String title; // 제목
        private String subtitle; // 부제목
        private String img; // 이미지 URL (S3 URL)
        private String description; // 설명
    }
}
