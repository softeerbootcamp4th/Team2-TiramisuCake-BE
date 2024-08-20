package com.softeer.backend.fo_domain.draw.dto.result;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 추첨 이벤트 당첨내역 조회 시 당첨내역이 없는 경우 응답하는 DTO 클래스
 */
@Data
@SuperBuilder
public class DrawHistoryLoserResponseDto extends DrawHistoryResponseDto {
    private String shareUrl; // 공유 url
}
