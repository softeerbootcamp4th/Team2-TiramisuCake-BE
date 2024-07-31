package com.softeer.backend.fo_domain.share.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShareUrlResponseDto {
    private String shareUrl;

    @Builder
    public ShareUrlResponseDto(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
