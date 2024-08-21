package com.softeer.backend.fo_domain.fcfs.dto.result;

import lombok.*;

/**
 * 선착순 성공 모달 정보를 담는 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
@Setter
public class FcfsSuccessResult implements FcfsResult {

    private String title;

    private String subTitle;

    private String qrCode;

    private String codeWord;

    private String fcfsCode;

    private String expirationDate;

    private String caution;

}
