package com.softeer.backend.fo_domain.fcfs.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

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
