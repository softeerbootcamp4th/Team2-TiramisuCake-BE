package com.softeer.backend.global.common.entity;

import com.softeer.backend.global.common.constant.RoleType;
import lombok.Builder;
import lombok.Getter;

/**
 * User 또는 Admin 유저의 인증 정보
 */
@Getter
@Builder
public class AuthInfo {
    // User 또는 Admin 테이블의 primary key
    private int id;
    // 유저의 권한 정보
    private RoleType roleType;
}
