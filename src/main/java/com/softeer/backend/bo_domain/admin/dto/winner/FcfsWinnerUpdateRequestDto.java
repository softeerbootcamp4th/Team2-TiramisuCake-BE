package com.softeer.backend.bo_domain.admin.dto.winner;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsWinnerUpdateRequestDto {

    private int round;

    private int fcfsWinnerNum;
}
