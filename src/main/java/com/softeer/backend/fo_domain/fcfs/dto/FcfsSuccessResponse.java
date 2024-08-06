package com.softeer.backend.fo_domain.fcfs.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsSuccessResponse implements FcfsResponse{
    private int a;
}
