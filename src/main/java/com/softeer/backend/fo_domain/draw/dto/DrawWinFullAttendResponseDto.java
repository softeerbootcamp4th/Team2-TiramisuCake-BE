package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DrawWinFullAttendResponseDto implements DrawResponseDto {
    private int invitedNum; // 내가 초대한 친구 수
    private int remainDrawCount; // 남은 복권 기회
    private int drawParticipationCount; // 연속 참여 일수
    private boolean isDrawWin; // 당첨됐는지 여부
    private List<String> images;
    private WinModal winModal;
    private FullAttendModal fullAttendModal;

    @Data
    @Builder
    public static class WinModal {
        private String title; // 제목
        private String subtitle; // 부제목
        private String img; // 이미지 URL (S3 URL)
        private String description; // 설명
    }

    @Data
    @Builder
    public static class FullAttendModal {

    }
}
