package com.softeer.backend.fo_domain.draw.dto.participate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 추첨 이벤트 낙첨 시 응답 DTO 클래스
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class DrawLoseModalResponseDto extends DrawModalResponseDto {
    private String shareUrl;
}
