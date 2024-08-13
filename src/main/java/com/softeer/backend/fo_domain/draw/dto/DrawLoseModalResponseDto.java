package com.softeer.backend.fo_domain.draw.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawLoseModalResponseDto extends DrawModalResponseDto {
    private String shareUrl;
}
