package com.softeer.backend.fo_domain.draw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DrawLoseFullAttendResponseDto extends DrawLoseResponseDto {
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
