package com.softeer.backend.fo_domain.user.dto.verification;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class VerificationCodeResponse {

    private int timeLimit;
}
