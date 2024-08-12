package com.softeer.backend.fo_domain.draw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DrawResponseDto {
    private int invitedNum; // 내가 초대한 친구 수
    private int remainDrawCount; // 남은 복권 기회
    private int drawParticipationCount; // 연속 참여 일수
    private boolean isDrawWin; // 당첨됐는지 여부
    private List<String> images; // 이미지 리스트
}
