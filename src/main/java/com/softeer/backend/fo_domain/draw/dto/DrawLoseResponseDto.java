package com.softeer.backend.fo_domain.draw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawLoseResponseDto extends DrawResponseDto {
    private LoseModal loseModal; // LoseModal 정보

    @Data
    @SuperBuilder
    public static class LoseModal {
        private String shareUrl; // 공유 url
    }
}
