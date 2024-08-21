package com.softeer.backend.fo_domain.draw.dto.history;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DrawHistoryDto {
    private int drawRank;
    private String image;
    private LocalDate winningDate;
}
