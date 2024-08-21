package com.softeer.backend.fo_domain.fcfs.dto;

import lombok.*;

/**
 * 선착순 등록 요청 Dto 클래스
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FcfsRequestDto {

    private String answer;
}
