package com.softeer.backend.fo_domain.user.constatnt;

import lombok.Getter;

@Getter
public enum RedisVerificationPrefix {
    VERIFICATION_CODE("VERIFICATION_CODE:"), // 인증코드의 Redis key prefix
    VERIFICATION_ISSUE_COUNT("VERIFICATION_ISSUE_COUNT:"), // 인증코드 발급 횟수의 Redis key prefix
    VERIFICATION_ATTEMPTS("VERIFICATION_ATTEMPTS:"); // 인증코드 시도 횟수의 Redis key prefix

    private final String prefix;

    RedisVerificationPrefix(String prefix) {
        this.prefix = prefix;
    }
}
