package com.softeer.backend.fo_domain.draw.dto.modal;

import lombok.Builder;
import lombok.Data;

/**
 * 추첨 이벤트 당첨 및 7일 연속 출석 시 상품 정보 응답 모달
 */
@Data
@Builder
public class WinModal {
    private String title; // 제목
    private String subtitle; // 부제목
    private String img; // 이미지 URL (S3 URL)
    private String description; // 설명
}
