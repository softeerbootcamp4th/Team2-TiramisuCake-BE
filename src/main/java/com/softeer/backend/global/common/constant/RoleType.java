package com.softeer.backend.global.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 유저의 권한 정보
 */
@Getter
@RequiredArgsConstructor
public enum RoleType {

    ROLE_USER("USER_"), // 일반 유저
    ROLE_ADMIN("ADMIN_"); // 관리자 유저

    String redisKeyPrefix;

    RoleType(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix;
    }
}
