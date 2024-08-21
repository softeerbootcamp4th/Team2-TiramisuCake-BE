package com.softeer.backend.fo_domain.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공유 url 응답 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareUrlInfoResponseDto {
    private String shareUrl;
}
