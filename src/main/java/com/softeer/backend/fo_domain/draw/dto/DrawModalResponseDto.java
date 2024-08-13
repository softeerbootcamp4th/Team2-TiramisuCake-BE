package com.softeer.backend.fo_domain.draw.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawModalResponseDto {
    private boolean isDrawWin; // 공유 url
}
