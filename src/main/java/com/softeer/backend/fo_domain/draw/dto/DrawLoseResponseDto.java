package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class DrawLoseResponseDto extends DrawResponseDto {
    private LoseModal loseModal; // LoseModal 정보

    @Data
    @Builder
    public static class LoseModal {
        private String shareUrl; // 공유 url
    }
}
