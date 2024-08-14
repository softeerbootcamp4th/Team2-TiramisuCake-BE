package com.softeer.backend.fo_domain.draw.dto.participate;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawLoseModalResponseDto extends DrawModalResponseDto {
    private String shareUrl;
}
