package com.softeer.backend.fo_domain.fcfs.dto.result;

import lombok.*;

/**
 * 선착순 실패 모달 정보를 담는 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class FcfsFailResult implements FcfsResult {

    private String title;

    private String subTitle;

    private String caution;
}
