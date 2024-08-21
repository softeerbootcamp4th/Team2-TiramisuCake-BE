package com.softeer.backend.fo_domain.user.dto.verification;

import lombok.*;

/**
 * 전화번호 인증 코드 요청 Test Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class VerificationCodeTestResponseDto {

    private String verificationCode;

    private int timeLimit;
}
