package com.softeer.backend.fo_domain.draw.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 추첨 이벤트 페이지 응답 DTO 클래스
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DrawMainResponseDto {
    private int invitedNum; // 내가 초대한 친구 수
    private int remainDrawCount; // 남은 복권 기회
    private int drawAttendanceCount; // 연속 참여 일수
}
