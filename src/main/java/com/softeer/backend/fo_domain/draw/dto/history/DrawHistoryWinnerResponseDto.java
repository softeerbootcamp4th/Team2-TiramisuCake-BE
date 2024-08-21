package com.softeer.backend.fo_domain.draw.dto.history;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 추첨 이벤트 당첨 내역이 있는 경우 응답 DTO 클래스
 */
@Data
@SuperBuilder
public class DrawHistoryWinnerResponseDto extends DrawHistoryResponseDto {
    private List<DrawHistoryDto> historyList;
}
