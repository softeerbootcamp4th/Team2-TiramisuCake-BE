package com.softeer.backend.fo_domain.user.constatnt;

import lombok.Getter;

/**
 * 전화번호 인증에서 사용되는 redis key의 prefix를 관리하는 enum 클래스
 */
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
