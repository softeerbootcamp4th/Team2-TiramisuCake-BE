package com.softeer.backend.fo_domain.draw.dto.participate;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class DrawModalResponseDto {
    private boolean isDrawWin; // 이겼는지 판단
    private List<String> images; // 방향 이미지 목록
}
