package com.softeer.backend.fo_domain.fcfs.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 선착순 설정 정보 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
public class FcfsSettingDto {

    private int round;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int winnerNum;
}
