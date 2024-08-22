package com.softeer.backend.bo_domain.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsSettingTestRequestDto {

    private int round;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int winnerNum;
}
