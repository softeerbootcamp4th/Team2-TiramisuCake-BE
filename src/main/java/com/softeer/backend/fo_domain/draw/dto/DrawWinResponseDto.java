package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DrawWinResponseDto implements DrawResponseDto {
    private int invitedNum; // 내가 초대한 친구 수
    private int remainDrawCount; // 남은 복권 기회
    private int drawParticipationCount; // 연속 참여 일수
    private boolean isDrawWin; // 당첨됐는지 여부
    private List<String> images;
    private WinModal winModal;

    @Builder
    public DrawWinResponseDto(int invitedNum, int remainDrawCount, int drawParticipationCount, boolean isDrawWin, List<String> images, WinModal winModal) {
        this.invitedNum = invitedNum;
        this.remainDrawCount = remainDrawCount;
        this.drawParticipationCount = drawParticipationCount;
        this.isDrawWin = isDrawWin;
        this.images = images;
        this.winModal = winModal;
    }
}
