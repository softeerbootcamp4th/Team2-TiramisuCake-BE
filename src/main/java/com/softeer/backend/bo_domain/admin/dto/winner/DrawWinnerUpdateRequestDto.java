package com.softeer.backend.bo_domain.admin.dto.winner;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class DrawWinnerUpdateRequestDto {

    private int firstWinnerNum;

    private int secondWinnerNum;

    private int thirdWinnerNum;
}
