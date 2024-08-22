package com.softeer.backend.bo_domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsSettingTestRequestDto {

    private int round;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime endTime;

    private int winnerNum;
}
