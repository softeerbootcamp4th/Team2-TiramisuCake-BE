package com.softeer.backend.fo_domain.fcfs.dto.result;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsResultResponseDto {

    private boolean isFcfsWinner;

    private FcfsResult fcfsResult;
}
