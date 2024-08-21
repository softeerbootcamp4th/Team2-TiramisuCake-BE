package com.softeer.backend.fo_domain.draw.dto.participate;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 추첨 이벤트 당첨 시 응답 DTO 클래스
 * WinModal 반환
 */
@Data
@SuperBuilder
public class DrawWinModalResponseDto extends DrawModalResponseDto {
    private WinModal winModal;
}
