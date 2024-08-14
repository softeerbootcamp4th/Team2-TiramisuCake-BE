package com.softeer.backend.fo_domain.fcfs.dto.result;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsFailResponseDto implements FcfsResponseDto {

    private String title;

    private String subTitle;

    private String caution;
}
