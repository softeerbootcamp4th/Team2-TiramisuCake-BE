package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DrawLoseResponseDto implements DrawResponseDto {
    private int invitedNum; // 내가 초대한 친구 수
    private int remainDrawCount; // 남은 복권 기회
    private int drawParticipationCount; // 연속 참여 일수
    private boolean isDrawWin; // 당첨됐는지 여부
    private List<String> images; // 이미지 리스트
    private LoseModal loseModal; // WinModal 정보

    @Builder
    public DrawLoseResponseDto(int invitedNum, int remainDrawCount, int drawParticipationCount, boolean isDrawWin, List<String> images, LoseModal loseModal) {
        this.invitedNum = invitedNum;
        this.remainDrawCount = remainDrawCount;
        this.drawParticipationCount = drawParticipationCount;
        this.isDrawWin = isDrawWin;
        this.images = images;
        this.loseModal = loseModal;
    }
}
