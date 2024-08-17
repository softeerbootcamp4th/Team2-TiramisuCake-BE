package com.softeer.backend.fo_domain.draw.dto.main;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawMainFullAttendResponseDto extends DrawMainResponseDto {
    private WinModal fullAttendModal;
}
