package com.softeer.backend.bo_domain.admin.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.softeer.backend.bo_domain.admin.validator.annotation.ValidDrawTimeRange;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 추첨 이벤트 시간 수정 요청 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@ValidDrawTimeRange
public class DrawEventTimeRequestDto {

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
}
