package com.softeer.backend.fo_domain.user.constatnt;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum VerificationProperty {
    TIME_LIMIT(300), // 인증코드 유효시간(단위: sec)
    CODE_LENGTH(6), // 인증코드의 길이
    MAX_ATTEMPTS(3), // 인증코드의 인증 제한 횟수
    CODE_ISSUE_ATTEMPTS(5); // 인증코드 발급 제한 횟수

    private final int value;

    VerificationProperty(int value) {
        this.value = value;
    }

}
