package com.softeer.backend.bo_domain.admin.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.softeer.backend.bo_domain.admin.validator.annotation.ValidFcfsDateRange;
import com.softeer.backend.bo_domain.admin.validator.annotation.ValidFcfsTimeRange;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@ValidFcfsDateRange
@ValidFcfsTimeRange
public class FcfsEventTimeRequestDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

}