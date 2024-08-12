package com.softeer.backend.fo_domain.draw.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DrawWinResponseDto extends DrawResponseDto {
    private WinModal winModal;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinModal {
        private String title; // 제목
        private String subtitle; // 부제목
        private String img; // 이미지 URL (S3 URL)
        private String description; // 설명
    }
}
