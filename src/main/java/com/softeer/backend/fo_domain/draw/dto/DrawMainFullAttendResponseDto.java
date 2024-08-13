package com.softeer.backend.fo_domain.draw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawMainFullAttendResponseDto extends DrawMainResponseDto {
    private DrawWinFullAttendResponseDto.FullAttendModal fullAttendModal;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FullAttendModal {
        private String title; // 제목
        private String subtitle; // 부제목
        private String image; // 이미지 URL (S3 URL)
        private String description; // 설명
    }
}
