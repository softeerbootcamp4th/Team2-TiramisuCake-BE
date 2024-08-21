package com.softeer.backend.fo_domain.user.dto.verification;

import lombok.*;

/**
 * 인증 코드 요청에 대한 응답 Dto 클래스
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class VerificationCodeResponseDto {

    private int timeLimit;
}
