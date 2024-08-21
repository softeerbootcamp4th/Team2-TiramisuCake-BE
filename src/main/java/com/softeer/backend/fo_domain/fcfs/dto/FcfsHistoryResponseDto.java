package com.softeer.backend.fo_domain.fcfs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 선착순 이벤트 당첨 기록 응답 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
public class FcfsHistoryResponseDto {

    @JsonProperty("isFcfsWin")
    private boolean isFcfsWin;

    private List<FcfsHistory> fcfsHistoryList;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class FcfsHistory {

        private String barcode;

        private String fcfsCode;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate winningDate;
    }


}
