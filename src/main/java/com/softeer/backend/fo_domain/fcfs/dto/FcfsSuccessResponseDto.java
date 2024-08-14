package com.softeer.backend.fo_domain.fcfs.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsSuccessResponseDto implements FcfsResponseDto {
    private int a;
}
