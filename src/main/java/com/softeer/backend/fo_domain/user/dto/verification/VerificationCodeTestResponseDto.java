package com.softeer.backend.fo_domain.user.dto.verification;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class VerificationCodeTestResponseDto {

    private String verificationCode;

    private int timeLimit;
}
