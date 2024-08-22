package com.softeer.backend.bo_domain.admin.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class DrawSettingTestRequestDto {

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private int winnerNum1;

    private int winnerNum2;

    private int winnerNum3;
}
