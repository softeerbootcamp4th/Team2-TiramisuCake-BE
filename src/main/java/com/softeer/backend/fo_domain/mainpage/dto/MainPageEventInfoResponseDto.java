package com.softeer.backend.fo_domain.mainpage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 메인 페이지 이벤트 정보를 응답하는 DTO 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class MainPageEventInfoResponseDto {

    private String startDate;

    private String endDate;

    private String fcfsInfo;

    private String drawInfo;

    private String totalDrawWinner;

    private String remainDrawCount;

    private String fcfsHint;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fcfsStartTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime drawStartTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime drawEndTime;
}
