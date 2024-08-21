package com.softeer.backend.fo_domain.draw.dto.history;

import com.softeer.backend.fo_domain.draw.dto.modal.WinModal;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class DrawHistoryWinnerResponseDto extends DrawHistoryResponseDto {
    private WinModal winModal;
}
