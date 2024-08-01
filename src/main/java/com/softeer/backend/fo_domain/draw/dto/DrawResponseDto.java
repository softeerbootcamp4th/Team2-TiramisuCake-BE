package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DrawResponseDto {
    private int invitedNum; // 내가 초대한 친구 수
    private int remainDrawCount; // 남은 복권 기회
    private int drawParticipationCount; // 연속 참여 일수

    @Builder
    public DrawResponseDto(int invitedNum, int remainDrawCount, int drawParticipationCount) {
        this.invitedNum = invitedNum;
        this.remainDrawCount = remainDrawCount;
        this.drawParticipationCount = drawParticipationCount;
    }
}
