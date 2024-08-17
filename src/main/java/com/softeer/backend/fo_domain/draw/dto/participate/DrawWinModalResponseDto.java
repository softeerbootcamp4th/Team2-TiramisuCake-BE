package com.softeer.backend.fo_domain.draw.dto.participate;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawWinModalResponseDto extends DrawModalResponseDto {
    private WinModal winModal;
}
