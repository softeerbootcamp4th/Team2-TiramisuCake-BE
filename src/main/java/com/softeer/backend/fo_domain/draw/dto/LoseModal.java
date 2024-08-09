package com.softeer.backend.fo_domain.draw.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoseModal {
    private String shareUrl; // 공유 url

    @Builder
    public LoseModal(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
