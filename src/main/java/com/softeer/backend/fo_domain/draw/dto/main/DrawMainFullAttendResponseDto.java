package com.softeer.backend.fo_domain.draw.dto.main;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 7일 연속 출석 시 추첨 이벤트 페이지 응답 DTO 클래스
 */
@Data
@SuperBuilder
public class DrawMainFullAttendResponseDto extends DrawMainResponseDto {
    private WinModal fullAttendModal;
}
