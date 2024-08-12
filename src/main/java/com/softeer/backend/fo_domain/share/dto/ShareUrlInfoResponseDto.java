package com.softeer.backend.fo_domain.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareUrlInfoResponseDto {
    private String shareUrl;
}
