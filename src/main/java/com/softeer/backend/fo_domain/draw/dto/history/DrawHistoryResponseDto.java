package com.softeer.backend.fo_domain.draw.dto.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 추첨 이벤트 당첨 내역을 응답하는 DTO 클래스
 */
@Data
@SuperBuilder
public class DrawHistoryResponseDto {
    @JsonProperty("isDrawWin")
    private boolean isDrawWin;
}
