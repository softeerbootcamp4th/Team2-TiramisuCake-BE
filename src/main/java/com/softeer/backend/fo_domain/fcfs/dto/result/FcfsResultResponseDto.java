package com.softeer.backend.fo_domain.fcfs.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 선착순 등록 결과 응답 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsResultResponseDto {

    @JsonProperty("isFcfsWinner")
    private boolean isFcfsWinner;

    private FcfsResult fcfsResult;
}
