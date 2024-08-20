package com.softeer.backend.fo_domain.fcfs.dto;

import lombok.*;

/**
 * 선착순 등록 요청 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
public class FcfsRequestDto {

    private String answer;
}
